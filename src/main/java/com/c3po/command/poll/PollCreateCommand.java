package com.c3po.command.poll;

import com.c3po.command.profile.Profile;
import com.c3po.connection.repository.AttributeRepository;
import com.c3po.core.DataFormatter;
import com.c3po.core.attribute.KnownAttribute;
import com.c3po.core.command.Context;
import com.c3po.core.command.SubCommand;
import com.c3po.core.openweatherapi.OpenWeatherMapApi;
import com.c3po.core.openweatherapi.endpoints.GetTemperature;
import com.c3po.core.openweatherapi.endpoints.Temperature;
import com.c3po.core.property.PropertyValue;
import com.c3po.helper.DateTimeHelper;
import com.c3po.helper.DiscordCommandOptionType;
import com.c3po.helper.EmbedHelper;
import com.c3po.helper.waiter.DateParser;
import com.c3po.helper.waiter.StringParser;
import com.c3po.service.ProfileService;
import com.c3po.ui.input.*;
import com.c3po.ui.input.base.Menu;
import com.c3po.ui.input.base.MenuManager;
import com.c3po.ui.input.base.MenuOption;
import com.c3po.ui.input.base.SubMenu;
import discord4j.core.spec.EmbedCreateFields;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Period;
import java.util.Collections;
import java.util.List;

public class PollCreateCommand extends SubCommand {
    protected PollCreateCommand(PollCommandGroup group) {
        super(group, "create", "Create a poll.");
//        this.addOption(option -> option.name("id")
//            .description("The identifier to accept")
//            .required(true)
//            .type(DiscordCommandOptionType.INTEGER.getValue()));
    }

    private void abc(PropertyValue propertyValue) {

    }

    private MenuOption getOptionFor(PropertyValue propertyValue) {
        if (propertyValue.getParentId() == KnownAttribute.dateOfBirthId) {
            return new WaiterMenuOption<>(
                "Date of birth",
                    new DateParser(),
                    propertyValue.getParsedValue()
            )
                .withEmoji("\uD83D\uDCC5")
            ;
        } else if (propertyValue.getParentId() == KnownAttribute.cityId) {
            return new WaiterMenuOption<>("Country",
                StringParser.builder().min(2).max(2).build(),
                propertyValue.getParsedValue())
                .withEmoji("\uD83D\uDDFA")
            ;
        } else if (propertyValue.getParentId() == KnownAttribute.countryId) {
            return new WaiterMenuOption<>("City", StringParser.builder().build(), propertyValue.getParsedValue())
                .withEmoji("\uD83D\uDCCD")
            ;
        }

        return null;
    }

    private Menu buildMenu(Context context, List<PropertyValue> propertyValues) {
        Menu menu = new Menu(context);

        for (PropertyValue propertyValue: propertyValues) {
            MenuOption menuOption = getOptionFor(propertyValue);
            if (menuOption == null) {
                continue;
            }
            menuOption.setSetter((v) -> {
                String value = DataFormatter.toRaw(v);
                propertyValue.setValue(value);
                return v;
            });
            menu.addOption(menuOption);
        }

        menu.addOption(new BackButtonMenuOption("Save").withEmoji("✔️"));
        return menu;
    }

    private EmbedCreateFields.Field profileToField(Profile profile) {
        String name = "name here";
        StringBuilder value = new StringBuilder();

        if (profile.getDateOfBirth() != null) {
            int age = Period.between(profile.getDateOfBirth(), DateTimeHelper.now().toLocalDate()).getYears();
            value.append("\nAge ").append(age);
        }

        OpenWeatherMapApi api = new OpenWeatherMapApi();
        try {
            Temperature response = api.call(GetTemperature.builder().cityName(profile.getCity()).countryCode(profile.getCountry()).build()).block();
            assert response != null;
            value.append("\nTemp ").append(response.getTemperature());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return EmbedCreateFields.Field.of(name, value.toString(), false);
    }

    @Override
    public Mono<?> execute(Context context) throws RuntimeException {
        Profile profile = ProfileService.getProfile(context.getTarget());

        context.getEvent().reply()
            .withEmbeds(EmbedHelper.normal("profile").addField(profileToField(profile)).build())
            .subscribe()
        ;


        List<PropertyValue> propertyValues = ProfileService.getEditableProfilePropertyValues(context.getTarget());
        CancelButtonMenuOption cancelOption = new CancelButtonMenuOption();

        Menu menu = buildMenu(context, propertyValues);
        menu.addOption(cancelOption);

        MenuManager.waitForMenu(menu, "Setup your profile").blockOptional();

        if (!cancelOption.isCancelled()) {
            AttributeRepository.db().save(propertyValues);
        }

        return context.getEvent().editReply().withEmbedsOrNull(Collections.singleton(EmbedHelper.normal("Profile finished setting up.").build()));

//        Menu menu = new Menu(context);
//        menu.addOption(new BooleanMenuOption("Anonymous").withEmoji("\uD83E\uDD77"));
//        menu.addOption(new BooleanMenuOption("Mention role").withEmoji("™️"));
//        menu.addOption(new BooleanMenuOption("Pin").withEmoji("\uD83D\uDCCC"));
//        menu.addOption(new BooleanMenuOption("Delete after results").withEmoji("\uD83D\uDDD1"));
//        menu.addOption(new SubMenuOption("Poll channel", new SubMenu(context, new ChannelMenuOption("Channels"))));
//        menu.addOption(new SubMenuOption("Result channel", new SubMenu(context, new ChannelMenuOption("Channels"))));
//        menu.addOption(new LongMenuOption("Max votes per user"));
//        return MenuManager.waitForMenu(menu, "OK");
    }

}
