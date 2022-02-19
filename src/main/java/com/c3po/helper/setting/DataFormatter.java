package com.c3po.helper.setting;

import com.c3po.helper.DataType;

public class DataFormatter {
    public static String prettify(DataType type, String value) {
        if (value == null) {
            return "n/a";
        }

        if (type.equals(DataType.BOOLEAN)) {
            return value.equals("1") ? "yes" : "no";
        }

        return value;
    }
}
