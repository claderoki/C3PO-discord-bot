package com.c3po.command.profile;

import com.c3po.connection.repository.AttributeRepository;
import com.c3po.core.DataFormatter;
import com.c3po.core.attribute.KnownAttribute;
import com.c3po.core.command.Context;
import com.c3po.core.property.PropertyValue;
import com.c3po.helper.EmbedHelper;
import com.c3po.helper.waiter.DateParser;
import com.c3po.helper.waiter.StringParser;
import com.c3po.helper.waiter.TimezoneParser;
import com.c3po.service.AttributeService;
import com.c3po.ui.input.BackButtonMenuOption;
import com.c3po.ui.input.CancelButtonMenuOption;
import com.c3po.ui.input.WaiterMenuOption;
import com.c3po.ui.input.base.Menu;
import com.c3po.ui.input.base.MenuManager;
import com.c3po.ui.input.base.MenuOption;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Component
public class ProfileSetupCommand extends ProfileSubCommand {
    private final AttributeService attributeService;
    private final AttributeRepository attributeRepository;

    protected ProfileSetupCommand(AttributeService attributeService, AttributeRepository attributeRepository) {
        super("setup", "Setup your profile.");
        this.attributeService = attributeService;
        this.attributeRepository = attributeRepository;
    }

    private MenuOption<?,?,?> getOptionFor(PropertyValue propertyValue) {
        if (propertyValue.getParentId() == attributeService.getId(KnownAttribute.dateOfBirthKey)) {
            return new WaiterMenuOption<>(
                "Date of birth",
                new DateParser(),
                propertyValue.getParsedValue()
            )
                .setEmoji("\uD83D\uDCC5")
                ;
        } else if (propertyValue.getParentId() == attributeService.getId(KnownAttribute.countryKey)) {
            return new WaiterMenuOption<>("Country",
                StringParser.builder().min(2).max(2).build(),
                propertyValue.getParsedValue())
                .setEmoji("\uD83D\uDDFA")
                ;
        } else if (propertyValue.getParentId() == attributeService.getId(KnownAttribute.cityKey)) {
            return new WaiterMenuOption<>("City", StringParser.builder().build(), propertyValue.getParsedValue())
                .setEmoji("\uD83D\uDCCD")
                ;
        } else if (propertyValue.getParentId() == attributeService.getId(KnownAttribute.timezoneKey)) {
            return new WaiterMenuOption<>("Timezone", TimezoneParser.builder().build(), propertyValue.getParsedValue())
                .setEmoji("\uD83D\uDCCD")
                ;
        }

        return null;
    }

    private Menu buildMenu(Context context, List<PropertyValue> propertyValues) {
        Menu menu = new Menu(context);

        for (PropertyValue propertyValue: propertyValues) {
            MenuOption<?, ?, ?> menuOption = getOptionFor(propertyValue);
            if (menuOption == null) {
                continue;
            }
            menuOption.setSetter(v -> {
                String value = DataFormatter.toRaw(v);
                propertyValue.setValue(value);
            });
            menu.addOption(menuOption);
        }

        menu.addOption(new BackButtonMenuOption("Save").setEmoji("✔️"));
        return menu;
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        List<PropertyValue> propertyValues = profileService.getEditableProfilePropertyValues(context.getTarget());
        CancelButtonMenuOption cancelOption = new CancelButtonMenuOption();

        Menu menu = buildMenu(context, propertyValues);
        menu.addOption(cancelOption);
        menu.setEmbedConsumer(e -> e.description("Setup your profile"));

        return new MenuManager<>(menu).waitFor().map(a -> {
            if (!cancelOption.isCancelled()) {
                attributeRepository.save(propertyValues);
            }
            return Mono.empty();
        }).then(context.getEvent().editReply()
                .withComponentsOrNull(null)
                .withEmbedsOrNull(Collections.singleton(EmbedHelper.normal("Profile finished setting up.").build()))
        ).then();
    }

}
