package com.c3po.command.profile;

import com.c3po.command.profile.fields.*;
import com.c3po.core.ScopeTarget;
import com.c3po.core.command.Context;
import com.c3po.core.openweatherapi.OpenWeatherMapApi;
import com.c3po.core.openweatherapi.endpoints.GetTemperature;
import com.c3po.core.openweatherapi.responses.Temperature;
import com.c3po.helper.DiscordCommandOptionType;
import com.c3po.helper.EmbedHelper;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.User;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateFields;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Optional;

@Component
public class ProfileViewCommand extends ProfileSubCommand {

    protected ProfileViewCommand() {
        super("view", "View your own, or someone else's profile.");
        this.addOption(option -> option.name("member")
            .description("The member who's profile you'd like to view.")
            .required(false)
            .type(DiscordCommandOptionType.USER.getValue()));
    }

    private Mono<Optional<Temperature>> getTemperatureFromProfile(Profile profile) {
        if (profile.getCity() == null) {
            return Mono.just(Optional.empty());
        }

        GetTemperature endpoint = GetTemperature.builder()
            .cityName(profile.getCity())
            .countryCode(profile.getCountry())
            .build();
        return new OpenWeatherMapApi().call(endpoint).map(Optional::of);
    }

    private String formatEmoji(ReactionEmoji emoji) {
        if (emoji.asEmojiData().id().isPresent()) {
            return emoji.asCustomEmoji().orElseThrow().asFormat();
        } else {
            return emoji.asUnicodeEmoji().orElseThrow().getRaw();
        }
    }

    private Mono<ArrayList<ProfileField<?>>> getFields(Profile profile) {
        ArrayList<ProfileField<?>> fields = new ArrayList<>();

        fields.add(new DateOfBirthField(profile.getDateOfBirth()));
        fields.add(new TimezoneField(profile.getTimezone()));
        fields.add(new PigeonNameField(profile.getPigeonName()));
        fields.add(new GoldField(profile.getGold()));

        if (profile instanceof MemberProfile memberProfile) {
            fields.add(new CloverField(memberProfile.getClovers()));
        }
        return getTemperatureFromProfile(profile).map(f -> {
                f.ifPresent(t -> fields.add(3, new TemperatureField(t)));
                return Mono.empty();
            })
            .then(Mono.just(fields));
    }

    private Mono<EmbedCreateFields.Field> profileToField(String username, Profile profile) {
        return getFields(profile).map(fields -> {
            StringBuilder value = new StringBuilder();
            for (ProfileField<?> field: fields) {
                if (field.isVisible()) {
                    value.append(formatEmoji(field.getEmoji())).append(" ").append(field.getParsedValue()).append("\n\n");
                }
            }
            return value.toString();
        }).map(v -> EmbedCreateFields.Field.of(username, v, false));
    }

    private Mono<User> getUser(Context context) {
        Snowflake userId = context.getOptions().optSnowflake("member");
        if (userId != null) {
            return context.getEvent().getClient().getUserById(userId);
        }
        return Mono.just(context.getEvent().getInteraction().getUser());
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        var optionalGuildId = context.getEvent().getInteraction().getGuildId();
        return getUser(context).flatMap(user -> {
            ScopeTarget target;
            target = optionalGuildId.map(snowflake -> ScopeTarget.member(user.getId().asLong(), snowflake.asLong()))
                .orElseGet(() -> ScopeTarget.user(user.getId().asLong()));

            Profile profile = profileService.getProfile(target);

            String username = user.getUsername();
            return profileToField(username, profile).flatMap(field -> context.getInteractor().reply()
                .withEmbeds(EmbedHelper.normal(null)
                    .addField(field)
                    .build())
                .then());
        });
    }

}
