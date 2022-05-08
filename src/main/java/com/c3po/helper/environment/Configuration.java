package com.c3po.helper.environment;

@lombok.Builder
@lombok.Getter
public class Configuration {
    private Mode mode;
    private String token;
    private String owmKey;
    private String wordnikKey;
    private DatabaseCredentials databaseCredentials;

    private static Configuration INSTANCE;

    public static void initiate(Configuration configuration) {
        INSTANCE = configuration;
    }

    public static Configuration instance() {
        return INSTANCE;
    }
}
