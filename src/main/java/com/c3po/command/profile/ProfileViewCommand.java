package com.c3po.command.profile;

import com.c3po.command.profile.fields.*;
import com.c3po.core.ScopeTarget;
import com.c3po.core.command.Context;
import com.c3po.core.command.SubCommand;
import com.c3po.core.openweatherapi.OpenWeatherMapApi;
import com.c3po.core.openweatherapi.endpoints.GetTemperature;
import com.c3po.core.openweatherapi.responses.Temperature;
import com.c3po.helper.DiscordCommandOptionType;
import com.c3po.helper.EmbedHelper;
import com.c3po.service.ProfileService;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.User;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateFields;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

public class ProfileViewCommand extends SubCommand {
    protected ProfileViewCommand(ProfileCommandGroup group) {
        super(group, "view", "View your own, or someone else's profile.");
        this.addOption(option -> option.name("member")
            .description("The member who's profile you'd like to view.")
            .required(false)
            .type(DiscordCommandOptionType.USER.getValue()));
    }
    private Temperature getTemperatureFromProfile(Profile profile) {
        if (profile.getCity() == null) {
            return null;
        }

        GetTemperature endpoint = GetTemperature.builder()
            .cityName(profile.getCity())
            .countryCode(profile.getCountry())
            .build();

        try {
            return new OpenWeatherMapApi().call(endpoint).block();
        } catch (Exception ignored) {

        }
        return null;
    }

    private String formatEmoji(ReactionEmoji emoji) {
        if (emoji.asEmojiData().id().isPresent()) {
            return emoji.asCustomEmoji().orElseThrow().asFormat();
        } else {
            return emoji.asUnicodeEmoji().orElseThrow().getRaw();
        }
    }

    private EmbedCreateFields.Field profileToField(String username, Profile profile) {
        ArrayList<ProfileField<?>> fields = new ArrayList<>();

        fields.add(new DateOfBirthField(profile.getDateOfBirth()));
        fields.add(new TimezoneField(profile.getTimezone()));
        fields.add(new PigeonNameField(profile.getPigeonName()));
        fields.add(new TemperatureField(getTemperatureFromProfile(profile)));
        fields.add(new GoldField(profile.getGold()));

        if (profile instanceof MemberProfile memberProfile) {
            fields.add(new CloverField(memberProfile.getClovers()));
        }

        StringBuilder value = new StringBuilder();
        for (ProfileField<?> field: fields) {
            if (field.isVisible()) {
                value.append(formatEmoji(field.getEmoji())).append(" ").append(field.getParsedValue()).append("\n\n");
            }
        }
        return EmbedCreateFields.Field.of(username, value.toString(), false);
    }

    @Override
    public Mono<?> execute(Context context) throws RuntimeException {
        var optionalGuildId = context.getEvent().getInteraction().getGuildId();
        Snowflake userId = context.getOptions().optSnowflake("member");

        User user = null;
        if (userId != null) {
            user = context.getEvent().getClient().getUserById(userId).block();
        }
        if (userId == null) {
            user = context.getEvent().getInteraction().getUser();
        }
        assert user != null;

        ScopeTarget target;
        if (optionalGuildId.isPresent()) {
            target = ScopeTarget.member(user.getId().asLong(), optionalGuildId.get().asLong());
        } else {
            target = ScopeTarget.user(user.getId().asLong());
        }

        Profile profile = ProfileService.getProfile(target);

        String username = user.getUsername();

        return context.getEvent().reply()
            .withEmbeds(EmbedHelper.normal(null)
                .addField(profileToField(username, profile))
                .build())
            .then();
    }

}
