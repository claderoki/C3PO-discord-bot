package com.c3po.command.image;

import com.c3po.connection.repository.KeyPoolRepository;
import com.c3po.core.api.FailedCallException;
import com.c3po.core.command.Context;
import com.c3po.core.openai.OpenAIApi;
import com.c3po.core.openai.endpoints.GenerateImage;
import com.c3po.core.openai.responses.B64Json;
import com.c3po.helper.DiscordCommandOptionType;
import com.c3po.helper.EmbedHelper;
import com.c3po.helper.TimedTrigger;
import com.c3po.model.keypool.Key;
import com.c3po.model.keypool.KeyPool;
import com.c3po.model.keypool.OutOfKeysException;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;

@Component
public class ImageCreateCommand extends ImageSubCommand {
    private final KeyPoolRepository keyPoolRepository;
    private KeyPool<Key> keyPool;
    TimedTrigger timedTrigger = new TimedTrigger(Duration.ofHours(1));

    record Collage(List<InputStream> sources, InputStream result) {}

    private final FileService fileService;

    protected ImageCreateCommand(KeyPoolRepository keyPoolRepository, FileService fileService) {
        super("generate", "Generate an image.");
        this.keyPoolRepository = keyPoolRepository;
        this.fileService = fileService;
        addOption(o -> o.name("prompt")
            .minLength(1)
            .type(DiscordCommandOptionType.STRING.getValue())
            .required(true)
            .description("Prompt..")
        );
    }

    private Mono<Collage> createCollage(List<InputStream> images) {
        if (images.isEmpty()) {
            return Mono.empty();
        }
        return Mono.just(new Collage(images, images.get(0)));
    }

    private Mono<Message> sendImages(Context context, String url) {
        return context.getInteractor().editReply()
            .withContentOrNull(null)
            .withEmbeds(EmbedHelper.normal(context.getOptions().getString("prompt"))
                .image(url)
                .build()
            );
    }

    private Mono<Void> call(String apiKey, Context context) {
        return new OpenAIApi(apiKey).call(GenerateImage.builder()
                .prompt(context.getOptions().getString("prompt"))
                .responseFormat(GenerateImage.ResponseFormat.b64_json)
                .size(GenerateImage.Size.large)
                .number(context.getOptions().optInt("amount", 1))
                .build())
            .onErrorResume(FailedCallException.class, e -> {
                if (e.getResponse().contains("billing_hard_limit_reached")) {
                    keyPoolRepository.invalidateKey("open_ai", apiKey);
                    keyPool.invalidateCurrent();
                    return context.getInteractor().editReply()
                        .withContentOrNull("Failed to generate image(s) because the current key expired.")
                        .then(Mono.empty());
                }
                return context.getInteractor().editReply()
                    .withContentOrNull("Failed to generate image(s), most likely this is because you have a prompt containing something OpenAI considers inappropriate.")
                    .then(Mono.empty());
            })
            .flux()
            .flatMap(r -> Flux.fromIterable(r.getB64s()))
            .map(B64Json::asStream)
            .collectList()
            .flatMap(this::createCollage)
            .flatMap(s -> fileService.store(s.result, "file.png"))
            .flatMap(u -> sendImages(context, u))
            .then();
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        timedTrigger.check(() -> keyPool = keyPoolRepository.getKeyPool("open_ai"));
        String apiKey;
        try {
            apiKey = keyPool.get();
        } catch (OutOfKeysException e) {
            return Mono.error(new Exception("Unfortunately the openAI key has expired."));
        }

        return context.getInteractor().reply()
            .withContent("Generating image(s) for `%s`".formatted(context.getOptions().getString("prompt")))
            .then(call(apiKey, context));
    }
}

