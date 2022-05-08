package com.c3po.command.profile;

import com.c3po.connection.repository.AttributeRepository;
import com.c3po.core.DataFormatter;
import com.c3po.core.attribute.KnownAttribute;
import com.c3po.core.command.Context;
import com.c3po.core.command.SubCommand;
import com.c3po.core.property.PropertyValue;
import com.c3po.helper.EmbedHelper;
import com.c3po.helper.waiter.DateParser;
import com.c3po.helper.waiter.StringParser;
import com.c3po.helper.waiter.TimezoneParser;
import com.c3po.service.ProfileService;
import com.c3po.ui.input.BackButtonMenuOption;
import com.c3po.ui.input.CancelButtonMenuOption;
import com.c3po.ui.input.WaiterMenuOption;
import com.c3po.ui.input.base.Menu;
import com.c3po.ui.input.base.MenuManager;
import com.c3po.ui.input.base.MenuOption;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

public class ProfileSetupCommand extends SubCommand {
    protected ProfileSetupCommand(ProfileCommandGroup group) {
        super(group, "setup", "Setup your profile.");
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
        } else if (propertyValue.getParentId() == KnownAttribute.timezoneId) {
            return new WaiterMenuOption<>("Timezone", TimezoneParser.builder().build(), propertyValue.getParsedValue())
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

    @Override
    public Mono<?> execute(Context context) throws RuntimeException {
        List<PropertyValue> propertyValues = ProfileService.getEditableProfilePropertyValues(context.getTarget());
        CancelButtonMenuOption cancelOption = new CancelButtonMenuOption();

        Menu menu = buildMenu(context, propertyValues);
        menu.addOption(cancelOption);
        menu.setEmbedConsumer(e -> e.description("Setup your profile"));

        MenuManager.waitForMenu(menu).blockOptional();

        if (!cancelOption.isCancelled()) {
            AttributeRepository.db().save(propertyValues);
        }

        return context.getEvent().editReply()
            .withComponentsOrNull(null)
            .withEmbedsOrNull(Collections.singleton(EmbedHelper.normal("Profile finished setting up.").build()));
    }

}
