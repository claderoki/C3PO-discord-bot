package com.c3po.ui;

import com.c3po.helper.DataType;
import discord4j.core.object.command.Interaction;

public class WaiterFactory {

    public static Waiter<?> getFor(DataType dataType, Interaction interaction) {
        return switch (dataType) {
            case INTEGER -> new IntWaiter(interaction);
            case STRING -> new StringWaiter(interaction);
            default -> throw new RuntimeException( dataType.getType() + " datatype not found");
        };
    }
}
