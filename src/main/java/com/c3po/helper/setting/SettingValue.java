package com.c3po.helper.setting;

import lombok.Builder;

@Builder
public class SettingValue {
    private Integer id;
    private Integer settingId;
    private SettingScopeTarget target;
    private String value;
    private String newValue;

    public void setValue(String value) {
        this.newValue = value;
    }

    public String getValue() {
        return newValue != null ? newValue : value;
    }

    public SettingScopeTarget getTarget() {
        return target;
    }

    public Integer getId() {
        return id;
    }

    public boolean changed() {
        return newValue != null;
    }

}
