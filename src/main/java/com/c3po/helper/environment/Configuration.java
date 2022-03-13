package com.c3po.helper.environment;

@lombok.Builder
@lombok.Getter
public class Configuration {
    private Mode mode;
    private String token;
    private DatabaseCredentials databaseCredentials;

    private static Configuration INSTANCE;

    public static void initiate(Configuration configuration) throws Exception {
        INSTANCE = configuration;
    }

    public static Configuration instance() {
        return INSTANCE;
    }
}
