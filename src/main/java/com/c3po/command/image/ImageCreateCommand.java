package com.c3po.command.image;

import com.c3po.core.command.Context;
import com.c3po.core.openai.OpenAIApi;
import com.c3po.core.openai.endpoints.GenerateImage;
import com.c3po.helper.DiscordCommandOptionType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ImageCreateCommand extends ImageSubCommand {
    protected ImageCreateCommand() {
        super("generate", "Generate an image.");
        addOption(o -> o.name("prompt")
            .type(DiscordCommandOptionType.STRING.getValue())
            .description("Prompt..")
        );
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        OpenAIApi api = new OpenAIApi();

        return context.getInteractor().reply().withContent("Generating...")
            .then(api.call(GenerateImage.builder().prompt(context.getOptions().getString("prompt")).build())
            .map(r -> r.getUrls().get(0))
            .flatMap(u -> context.getInteractor().editReply().withContentOrNull(u))
            .then());
    }
}
