package com.c3po.model;

public class BaseModel {
    public String optString(String value) {
        if (value == null) {
            return null;
        }
        return value;
    }

    public Long optLong(String value) {
        if (value == null) {
            return null;
        }
        return Long.parseLong(value);
    }

    public Integer optInt(String value) {
        if (value == null) {
            return null;
        }
        return Integer.parseInt(value);
    }

    public Integer getInt(String value) {
        return Integer.parseInt(value);
    }

    public long getLong(String value) {
        return Long.parseLong(value);
    }

    public boolean getBool(String value) {
        return value.equals("1");
    }

}
