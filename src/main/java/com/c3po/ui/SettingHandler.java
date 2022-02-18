package com.c3po.ui;

import com.c3po.helper.setting.*;
import discord4j.core.event.domain.interaction.DeferrableInteractionEvent;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SettingHandler {
    private final SettingScopeTarget target;
    private final String category;
    ArrayList<Setting> settings;
    HashMap<Integer, SettingValue> values;
    private boolean replied = false;

    public SettingHandler(SettingScopeTarget target, String category) {
        this.target = target;
        this.category = category;
    }

    private void load() throws SQLException {
//        settings = SettingCache.getSettings(category);
//        values = SettingValueLoader.load(target, category);
    }

//    private static EmbedCreateSpec createEmbedFor(Setting setting) {
//        return EmbedCreateSpec.builder()
//                .color(Color.of(242, 180, 37))
//                .description(setting.getPrompt())
//        .build();
//    }

//    public Mono<Void> handle(DeferrableInteractionEvent event) throws Exception {
//        if (settings == null) {
//            load();
//        }
//
//        event.reply().withContent("Question").block();
//
//        for (Setting setting: settings) {
//            if (!setting.isExposed()) {
//                continue;
//            }
//
//            Waiter<?> waiter = WaiterFactory.getFor(setting.getType(), event.getInteraction());
//
//            event.editReply()
//                    .withEmbeds(createEmbedFor(setting))
//                    .then(
//                    waiter.handle().doFinally((c) -> {
//                        SettingValue value = values.get(setting.getId());
//                        if (value != null) {
//                            value.setValue(waiter.getValue().toString());
//                        }
//                    }).then()
//            ).block();
//        }
//        return Mono.empty();
//    }

}
