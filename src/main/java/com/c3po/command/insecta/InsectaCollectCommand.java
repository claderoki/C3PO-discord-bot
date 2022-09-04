package com.c3po.command.insecta;

import com.c3po.core.command.Context;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class InsectaCollectCommand extends InsectaSubCommand {
    protected InsectaCollectCommand() {
        super("collect");
    }

    @Override
    protected Mono<Void> execute(Context context) throws RuntimeException {
        return Mono.empty();
    }
}
