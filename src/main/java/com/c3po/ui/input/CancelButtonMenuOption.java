package com.c3po.ui.input;

public class CancelButtonMenuOption extends BooleanMenuOption {
    public CancelButtonMenuOption(String name) {
        super(name);
    }

    public CancelButtonMenuOption() {
        super("Cancel");
        emoji = "❌";
    }

    public boolean isCancelled() {
        return getValue() != null && getValue();
    }

    public boolean shouldContinue() {
        return false;
    }

}
