package com.c3po.command.image;

import com.c3po.core.command.Context;
import com.c3po.core.openai.OpenAIApi;
import com.c3po.core.openai.endpoints.GenerateImage;
import com.c3po.core.openai.responses.B64Json;
import com.c3po.helper.DiscordCommandOptionType;
import com.c3po.helper.EmbedHelper;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class ImageCreateCommand extends ImageSubCommand {
    record Collage(List<InputStream> sources, InputStream result) {}

    private final FileService fileService;

    protected ImageCreateCommand(FileService fileService) {
        super("generate", "Generate an image.");
        addOption(o -> o.name("prompt")
            .minLength(1)
            .type(DiscordCommandOptionType.STRING.getValue())
            .required(true)
            .description("Prompt..")
        );
        addOption(o -> o.name("amount")
            .type(DiscordCommandOptionType.INTEGER.getValue())
            .required(false)
            .maxLength(4)
            .description("The amount of images")
        );

        this.fileService = fileService;
    }

    private Mono<Collage> createCollage(List<InputStream> images) {
        if (images.size() == 1) {
            return Mono.just(new Collage(images, images.get(0)));
        }
        List<BufferedImage> bufferedImages = new ArrayList<>();
        for(var img: images) {
            try {
                bufferedImages.add(ImageIO.read(img));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        int width = bufferedImages.get(0).getWidth();
        int height = bufferedImages.get(0).getHeight();
        if (bufferedImages.size() >= 3) {
            width *= 2;
        }
        if (bufferedImages.size() == 4) {
            height *= 2;
        }
        BufferedImage collage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = collage.getGraphics();

        g.drawImage(bufferedImages.get(0), 0, 0, null);
        g.drawImage(bufferedImages.get(1), bufferedImages.get(0).getWidth(), 0, null);
        if (bufferedImages.size() >= 3) {
            g.drawImage(bufferedImages.get(2), 0, bufferedImages.get(0).getHeight(), null);
        }
        if (bufferedImages.size() == 4) {
            g.drawImage(bufferedImages.get(3), bufferedImages.get(0).getWidth(), bufferedImages.get(0).getHeight(), null);
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(collage, "jpg", out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Mono.just(new Collage(images, new ByteArrayInputStream(out.toByteArray())));
    }

    private Mono<Message> sendImages(Context context, String url) {
        return context.getInteractor().editReply()
            .withContentOrNull(null)
            .withEmbeds(EmbedHelper.normal(context.getOptions().getString("prompt"))
                .image(url)
                .build()
            );
    }

    private Mono<Collage> call(Context context) {
        return new OpenAIApi().call(GenerateImage.builder()
                .prompt(context.getOptions().getString("prompt"))
                .responseFormat(GenerateImage.ResponseFormat.b64_json)
                .size(GenerateImage.Size.large)
                .number(context.getOptions().optInt("amount", 1))
                .build())
            .onErrorResume(e -> context.getInteractor().editReply()
                .withContentOrNull("Failed to generate image(s), most likely this is because you have a prompt containing something OpenAI considers inappropriate.")
                .then(Mono.empty()))
            .flux()
            .flatMap(r -> Flux.fromIterable(r.getB64s()))
            .map(B64Json::asStream)
            .collectList()
            .flatMap(this::createCollage)
            .flatMap(s -> fileService.store(s.result, "file.png")
                .flatMap(u -> sendImages(context, u))
                .then(Mono.just(s))
            );
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        return context.getInteractor().reply()
            .withContent("Generating image(s) for `%s`".formatted(context.getOptions().getString("prompt")))
            .then(call(context))
            .then();
    }
}

