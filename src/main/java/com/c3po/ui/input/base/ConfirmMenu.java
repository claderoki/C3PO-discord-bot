package com.c3po.ui.input.base;

import com.c3po.core.command.Context;
import com.c3po.ui.input.DynamicButtonMenuOption;
import discord4j.core.object.component.Button;
import lombok.Getter;
import reactor.core.publisher.Mono;

@Getter
public class ConfirmMenu extends Menu {
    private final boolean warning;

    private Boolean confirmed = null;

    public ConfirmMenu(Context context, boolean warning) {
        super(context);
        this.warning = warning;
        setMaximumOptionsAllowed(1);
        addOption(getCancelOption());
        addOption(getConfirmOption());
    }

    public ConfirmMenu(Context context) {
        this(context, false);
    }

    private DynamicButtonMenuOption getCancelOption() {
        return new DynamicButtonMenuOption("No")
            .onClick(e -> Mono.fromRunnable(() -> confirmed = false))
            .setButtonStyle(warning ? Button.Style.SUCCESS : Button.Style.PRIMARY);
    }

    private DynamicButtonMenuOption getConfirmOption() {
        return new DynamicButtonMenuOption("Yes")
            .onClick(e -> Mono.fromRunnable(() -> confirmed = true))
            .setButtonStyle(warning ? Button.Style.DANGER : Button.Style.PRIMARY);
    }
}
