package com.c3po.helper;

import com.c3po.helper.environment.Configuration;
import com.c3po.helper.environment.Mode;

public class LogHelper {

    public static void log(Throwable e) {
        System.out.printf("[%s] ERROR OCCURED: ", getDate());
        e.printStackTrace();
    }

    public static void log(Throwable e, String identifier) {
        System.out.printf("[%s] ERROR OCCURED %s: ", getDate(), identifier);
        e.printStackTrace();
    }

    public static void log(String message) {
        System.out.printf("[%s] %s%n", getDate(), message);
    }

    private static String getDate() {
        return DateTimeHelper.now().format(DateTimeHelper.DATETIME_FORMATTER);
    }

    private static boolean isInScope(LogScope scope) {
        return switch (scope) {
            case DEVELOPMENT -> Configuration.instance().getMode().equals(Mode.DEVELOPMENT);
            case PRODUCTION -> Configuration.instance().getMode().equals(Mode.PRODUCTION);
            case ALL -> true;
        };
    }

    public static void log(String message, LogScope scope) {
        if (!isInScope(scope)) {
            return;
        }
        log(message);
    }

}