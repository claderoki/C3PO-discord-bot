package com.c3po.command.image;

import com.c3po.core.command.Context;
import com.c3po.core.openai.OpenAIApi;
import com.c3po.core.openai.endpoints.GenerateImage;
import com.c3po.core.openai.responses.B64Json;
import com.c3po.helper.DiscordCommandOptionType;
import com.c3po.helper.EmbedHelper;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ImageCreateCommand extends ImageSubCommand {
    private final FileService fileService;

    protected ImageCreateCommand(FileService fileService) {
        super("generate", "Generate an image.");
        addOption(o -> o.name("prompt")
            .type(DiscordCommandOptionType.STRING.getValue())
            .required(true)
            .description("Prompt..")
        );
        this.fileService = fileService;
    }

    private Mono<Message> sendImage(Context context, String prompt, String url) {
        return context.getInteractor().editReply()
            .withContentOrNull(null)
            .withEmbeds(EmbedHelper.normal(prompt)
                .image(url)
                .build()
            );
    }

    private Mono<Void> call(Context context, String prompt) {
        return new OpenAIApi().call(GenerateImage.builder().prompt(prompt).responseFormat("b64_json").build())
            .onErrorContinue((e,v) -> context.getInteractor().editReply().withContentOrNull("Failed."))
            .map(r -> r.getB64s().get(0))
            .map(B64Json::asStream)
            .flatMap(s -> fileService.store(s, "file.png"))
            .flatMap(u -> sendImage(context, prompt, u).then(Mono.just(u)))
            .then();
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        String prompt = context.getOptions().getString("prompt");
        return context.getInteractor().reply().withContent("Generating an image for `%s`".formatted(prompt))
            .then(call(context, prompt));
    }
}

