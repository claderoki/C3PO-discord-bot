package com.c3po.helper.setting.validation;

public class SettingValidationFormatter {

    public static String getError(String from, String to, Condition condition) {
        return "`%s` needs to be %s `%s`".formatted(from, getConditionText(condition), to);
    }

    private static String getConditionText(Condition condition) {
        return switch (condition) {
            case GT -> "greater than";
            case GTE -> "greater than or equal to";
            case LT -> "less than";
            case LTE -> "less than or equal to";
        };
    }

}
