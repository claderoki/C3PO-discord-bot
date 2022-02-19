package com.c3po.helper.setting.validation;

import java.util.ArrayList;

public class SettingValidationResult {
    private final ArrayList<String> errors = new ArrayList<>();

    public void addError(String error) {
        errors.add(error);
    }

    public ArrayList<String> getErrors() {
        return errors;
    }
}
