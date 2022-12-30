package com.c3po.command.image;

import com.c3po.core.command.Context;
import com.c3po.core.openai.OpenAIApi;
import com.c3po.core.openai.endpoints.GenerateImage;
import com.c3po.helper.DiscordCommandOptionType;
import com.c3po.helper.EmbedHelper;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class ImageCreateCommand extends ImageSubCommand {
    private FileService fileService;

    protected ImageCreateCommand() {
        super("generate", "Generate an image.");
        addOption(o -> o.name("prompt")
            .type(DiscordCommandOptionType.STRING.getValue())
            .required(true)
            .description("Prompt..")
        );
    }

    private FileService getFileService(GatewayDiscordClient client) {
        if (fileService == null) {
            fileService = new FileService(client);
        }
        return fileService;
    }

    private Mono<Message> sendImage(Context context, String prompt, String url) {
        return context.getInteractor().editReply()
            .withContentOrNull(null)
            .withEmbeds(EmbedHelper.normal(prompt)
                .image(url)
                .build()
            );
    }

    private Mono<Void> postStore(Context context, String prompt, String url) {
        return getFileService(context.getEvent().getClient()).store(url)
            .flatMap(u -> sendImage(context, prompt, u))
            .then();
    }

    private Mono<Void> call(Context context, String prompt) {
        return new OpenAIApi().call(GenerateImage.builder().prompt(prompt).build())
            .onErrorContinue((e,v) -> context.getInteractor().editReply().withContentOrNull("Failed."))
            .map(r -> r.getUrls().get(0))
            .flatMap(u -> sendImage(context, prompt, u).then(Mono.just(u)))
            .doOnSuccess(u -> postStore(context, prompt, u).delaySubscription(Duration.ofMinutes(5)).subscribe())
            .then();
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        String prompt = context.getOptions().getString("prompt");
        return context.getInteractor().reply().withContent("Generating an image for `%s`".formatted(prompt))
            .then(call(context, prompt));
    }
}

