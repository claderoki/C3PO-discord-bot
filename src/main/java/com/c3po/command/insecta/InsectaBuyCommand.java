package com.c3po.command.insecta;

import com.c3po.command.insecta.core.Insecta;
import com.c3po.command.insecta.core.Mosquito;
import com.c3po.connection.repository.AttributeRepository;
import com.c3po.connection.repository.InsectaRepository;
import com.c3po.core.ScopeTarget;
import com.c3po.core.attribute.KnownAttribute;
import com.c3po.core.command.Context;
import com.c3po.core.property.PropertyValue;
import com.c3po.helper.DiscordCommandOptionType;
import com.c3po.service.AttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class InsectaBuyCommand extends InsectaSubCommand {
    @Autowired
    protected InsectaRepository insectaRepository;

    protected InsectaBuyCommand() {
        super("buy");
        addOption(o -> o
            .name("type")
            .type(DiscordCommandOptionType.STRING.getValue())
            .required(true));
    }

    @Override
    protected Mono<Void> execute(Context context) {
        String type = context.getOptions().getString("type");
//        Insecta insecta = insectas.get(type);
//        if (insecta == null) {
//            return Mono.error(new Exception("Not valid."));
//        }

        // before buying, 'Collect' the current winnings without showing the user until they do /collect.
        return Mono.empty();
    }
}
