package com.c3po.command.milkyway;

import com.c3po.connection.repository.AttributeRepository;
import com.c3po.core.attribute.KnownAttribute;
import com.c3po.core.command.CommandGroup;
import com.c3po.core.command.Context;
import com.c3po.connection.repository.ItemRepository;
import com.c3po.connection.repository.MilkywayRepository;
import com.c3po.core.command.SubCommand;
import com.c3po.core.property.PropertyValue;
import com.c3po.errors.PublicException;
import com.c3po.helper.DiscordCommandOptionType;
import com.c3po.model.milkyway.Milkyway;
import com.c3po.model.milkyway.MilkywayStatus;
import com.c3po.service.AttributeService;
import com.c3po.service.HumanService;
import reactor.core.publisher.Mono;

public class MilkywayDenyCommand extends SubCommand {
    protected MilkywayDenyCommand(CommandGroup group) {
        super(group, "deny", "Deny a milkyway");
        this.addOption(option -> option.name("id")
            .description("The identifier to deny")
            .required(true)
            .type(DiscordCommandOptionType.INTEGER.getValue()));
        this.addOption(option -> option.name("reason")
            .description("The reason why this milkyway was denied.")
            .required(true)
            .type(DiscordCommandOptionType.STRING.getValue()));
    }

    private void givebackPayment(Milkyway milkyway) {
        switch (milkyway.getPurchaseType()) {
            case POINT -> {
                PropertyValue cloverAttributeValue = AttributeService.getAttributeValue(milkyway.getTarget(), AttributeService.getId(KnownAttribute.CLOVERS));
                cloverAttributeValue.increment(milkyway.getAmount());
                AttributeRepository.db().save(cloverAttributeValue);
            }
            case ITEM -> ItemRepository.db().addItem(HumanService.getHumanId(milkyway.getTarget().getUserId()), milkyway.getItemId(), milkyway.getAmount());
        }
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        long identifier = context.getOptions().getLong("id");
        String reason = context.getOptions().getString("reason");

        long guildId = context.getEvent().getInteraction().getGuildId().orElseThrow().asLong();
        Milkyway milkyway = MilkywayRepository.db().get(guildId, identifier);
        if (milkyway.getId() == 0) {
            throw new PublicException("This milkyway does not exist.");
        } else if (!milkyway.getStatus().equals(MilkywayStatus.PENDING)) {
            throw new PublicException("This milkyway can't be denied anymore.");
        }

        MilkywayRepository.db().deny(guildId, identifier, reason);
        givebackPayment(milkyway);

        context.getEvent().getInteraction().getUser().getPrivateChannel().subscribe((c) -> c.createMessage(
            "Your milkyway request has been denied, reason: " + reason
        ).then());

        return context.getEvent().reply().withContent("OK.");
    }
}
