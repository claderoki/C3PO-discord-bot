package com.c3po.helpers.environment;

@lombok.Builder
@lombok.Getter
public class Configuration {
    private Mode mode;
    private String token;
    private DatabaseCredentials databaseCredentials;
}
