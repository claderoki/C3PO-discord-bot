package com.c3po.helper.environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class ConfigurationLoader {

    private static HashMap<String, String> getEnvironmentalVariables() throws Exception {
        HashMap<String, String> map = new HashMap<>();
        try {
            File myObj = new File(".env");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (!data.isBlank()) {
                    String[] values = data.split("=");
                    map.put(values[0], values[1]);
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            throw new Exception(".env file not found.");
        }

        return map;
    }

    public static Configuration load(Mode mode) throws Exception {
        HashMap<String, String> map = getEnvironmentalVariables();
        return Configuration.builder()
                .databaseCredentials(DatabaseCredentials.builder()
                        .database(map.get("mysql_db_name"))
                        .host(map.get("mysql_host"))
                        .username(map.get("mysql_user"))
                        .password(map.get("mysql_password"))
                        .port(Integer.parseInt(map.get("mysql_port")))
                        .build())
                .mode(mode)
                .token(map.get("discord_token_" + mode.getType()))
                .build();
    }

}
