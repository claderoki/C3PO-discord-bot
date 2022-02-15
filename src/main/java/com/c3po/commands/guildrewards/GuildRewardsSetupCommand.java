package com.c3po.commands.guildrewards;

import com.c3po.commands.Command;
import com.c3po.database.DataSourceLoader;
import com.c3po.database.repositories.GuildRewardsRepository;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

import javax.sql.DataSource;
import java.sql.SQLException;

public class GuildRewardsSetupCommand extends Command {
    public String getName() {
        return "guildrewards setup";
    }

    public Mono<Void> handle(ChatInputInteractionEvent event) {

        DataSource ds = DataSourceLoader.instance();
        try {
            ds.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            GuildRewardsRepository.db().getSettings();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return event.reply()
                .withEphemeral(true)
                .withContent("Hello");
    }
}
