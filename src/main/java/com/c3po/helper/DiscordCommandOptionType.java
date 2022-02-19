package com.c3po.helper;

public enum DiscordCommandOptionType {
    SUB_COMMAND(1),
    SUB_COMMAND_GROUP(2),
    STRING(3),
    INTEGER(4),
    BOOLEAN(5),
    USER(6),
    CHANNEL(7),
    ROLE(8),
    MENTIONABLE(9),
    NUMBER(10),
    ATTACHMENT(11);

    private int value;
    DiscordCommandOptionType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
