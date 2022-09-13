package com.c3po.command.battle.action.core;

import reactor.core.publisher.Mono;

public abstract class Action<C extends ActionContext, R extends ActionResult> {
    public abstract String getName();

    public abstract Mono<R> execute(C context);
}
