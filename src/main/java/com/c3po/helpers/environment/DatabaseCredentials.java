package com.c3po.helpers.environment;

@lombok.Builder
@lombok.Getter
public class DatabaseCredentials {
    private String host;
    private String database;
    private String username;
    private String password;
    private int port;
}
