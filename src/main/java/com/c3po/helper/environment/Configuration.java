package com.c3po.helper.environment;

@lombok.Builder
@lombok.Getter
public class Configuration {
    private Mode mode;
    private String token;
    private DatabaseCredentials databaseCredentials;

    private static Configuration INSTANCE;

    public static void initiate(Mode mode) throws Exception {
        INSTANCE = ConfigurationLoader.load(mode);
    }

    public static Configuration instance() {
        return INSTANCE;
    }
}
