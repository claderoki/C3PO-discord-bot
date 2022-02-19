package com.c3po.helper.setting.validation;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SettingValidation {
    private int id;
    private int settingId;
    private Condition condition;
    private ValueType valueType;
    private String value;
}
