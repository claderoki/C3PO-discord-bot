package com.c3po.processors;

import com.c3po.connection.repository.MilkywayRepository;
import com.c3po.helper.LogHelper;
import com.c3po.model.milkyway.ExpiredMilkyway;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class MilkywayExpirationChecker implements Runnable {
    private DiscordClient client;

    public void run() {
    }
}
