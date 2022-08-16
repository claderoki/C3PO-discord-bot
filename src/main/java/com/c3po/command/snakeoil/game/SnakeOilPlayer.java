package com.c3po.command.snakeoil.game;

import discord4j.core.object.entity.User;

public record SnakeOilPlayer(User user, Deck deck) {
}
