package com.c3po.ui.input.base;

import com.c3po.core.command.Context;
import com.c3po.ui.input.DynamicButtonMenuOption;
import discord4j.core.object.component.Button;
import lombok.Getter;
import reactor.core.publisher.Mono;

@Getter
public class ConfirmMenu extends Menu {
    private Boolean confirmed = null;

    public ConfirmMenu(Context context) {
        super(context);
        setMaximumOptionsAllowed(1);
        this.addOption(new DynamicButtonMenuOption("No")
            .onClick(e -> Mono.fromRunnable(() -> confirmed = false))
        );
        this.addOption(new DynamicButtonMenuOption("Yes")
            .onClick(e -> Mono.fromRunnable(() -> confirmed = true))
            .setButtonStyle(Button.Style.SUCCESS)
        );
    }
}
