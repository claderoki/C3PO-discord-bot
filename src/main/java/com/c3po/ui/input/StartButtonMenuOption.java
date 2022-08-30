package com.c3po.ui.input;

public class StartButtonMenuOption extends BooleanMenuOption {
    public StartButtonMenuOption(String name) {
        super(name);
    }

    public boolean shouldContinue() {
        return false;
    }

    protected boolean isBottomRow() {
        return false;
    }
}
