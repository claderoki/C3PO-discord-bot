package com.c3po.command.milkyway;

import com.c3po.connection.repository.AttributeRepository;
import com.c3po.connection.repository.ItemRepository;
import com.c3po.core.attribute.KnownAttribute;
import com.c3po.core.command.Context;
import com.c3po.core.property.PropertyValue;
import com.c3po.error.PublicException;
import com.c3po.helper.DiscordCommandOptionType;
import com.c3po.model.milkyway.Milkyway;
import com.c3po.model.milkyway.MilkywayStatus;
import com.c3po.service.AttributeService;
import com.c3po.service.HumanService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class MilkywayDenyCommand extends MilkywaySubCommand {
    private final HumanService humanService;
    private final AttributeService attributeService;
    private final AttributeRepository attributeRepository;
    private final ItemRepository itemRepository;

    protected MilkywayDenyCommand(HumanService humanService, AttributeService attributeService, AttributeRepository attributeRepository, ItemRepository itemRepository) {
        super("deny", "Deny a milkyway");
        this.addOption(option -> option.name("id")
            .description("The identifier to deny")
            .required(true)
            .type(DiscordCommandOptionType.INTEGER.getValue()));
        this.addOption(option -> option.name("reason")
            .description("The reason why this milkyway was denied.")
            .required(true)
            .type(DiscordCommandOptionType.STRING.getValue()));
        this.humanService = humanService;
        this.attributeService = attributeService;
        this.attributeRepository = attributeRepository;
        this.itemRepository = itemRepository;
    }

    private void givebackPayment(Milkyway milkyway) {
        switch (milkyway.getPurchaseType()) {
            case POINT -> {
                PropertyValue cloverAttributeValue = attributeService.getAttributeValue(milkyway.getTarget(), attributeService.getId(KnownAttribute.cloverKey));
                cloverAttributeValue.increment(milkyway.getAmount());
                attributeRepository.save(cloverAttributeValue);
            }
            case ITEM -> itemRepository.addItem(humanService.getHumanId(milkyway.getTarget().getUserId()), milkyway.getItemId(), milkyway.getAmount());
        }
    }

    @Override
    public Mono<Void> execute(Context context) throws RuntimeException {
        long identifier = context.getOptions().getLong("id");
        String reason = context.getOptions().getString("reason");

        long guildId = context.getEvent().getInteraction().getGuildId().orElseThrow().asLong();
        Milkyway milkyway = milkywayRepository.get(guildId, identifier);
        if (milkyway.getId() == 0) {
            throw new PublicException("This milkyway does not exist.");
        } else if (!milkyway.getStatus().equals(MilkywayStatus.PENDING)) {
            throw new PublicException("This milkyway can't be denied anymore.");
        }

        milkywayRepository.deny(guildId, identifier, reason);
        givebackPayment(milkyway);

        return context.getEvent().getInteraction().getUser().getPrivateChannel().flatMap((c) -> c.createMessage(
            "Your milkyway request has been denied, reason: " + reason
        ).and(context.getEvent().reply().withContent("OK.")));
    }
}
