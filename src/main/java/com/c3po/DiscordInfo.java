package com.c3po;

import discord4j.core.GatewayDiscordClient;
import org.springframework.stereotype.Component;

@Component
public class DiscordInfo {
    private static GatewayDiscordClient client;

    public static void initiate(GatewayDiscordClient client) {
        DiscordInfo.client = client;
    }

    public GatewayDiscordClient getClient() {
        return client;
    }

}
