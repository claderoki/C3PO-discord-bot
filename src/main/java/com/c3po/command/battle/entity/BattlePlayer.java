package com.c3po.command.battle.entity;

import com.c3po.command.battle.game.PlayerStatus;
import discord4j.core.object.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BattlePlayer extends Player {
    private boolean finished;
    private PlayerStatus status = PlayerStatus.UNDECIDED;

    public BattlePlayer(User user) {
        super(user);
    }
}