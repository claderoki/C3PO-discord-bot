package com.c3po.helper.setting;

import com.c3po.helper.DataType;
import lombok.Builder;

@Builder
public class SettingValue {
    private Integer id;
    private Integer settingId;
    private SettingScopeTarget target;
    private DataType type;
    private String value;
    private String newValue;

    public void setValue(String value) {
        if (this.value == null) {
            this.value = this.newValue;
        }
        this.newValue = value;
    }

    public String getValue() {
        return newValue != null ? newValue : value;
    }

    public String getOriginalValue() {
        return value;
    }

    public SettingScopeTarget getTarget() {
        return target;
    }

    public Integer getId() {
        return id;
    }

    public Integer getSettingId() {
        return settingId;
    }

    public DataType getType() {
        return type;
    }

    public boolean changed() {
        return newValue != null && !newValue.equals(value);
    }

}
