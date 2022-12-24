package com.c3po.helper.environment;

import com.c3po.helper.ValueParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class ConfigurationLoader {
    private record Env(HashMap<String, String> map) implements ValueParser {
        @Override
        public String optString(String key) {
            return map.get(key);
        }
    }

    private static Env getEnvironmentalVariables() throws Exception {
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

        return new Env(map);
    }

    public static Configuration load(Mode mode) throws Exception {
        var env = getEnvironmentalVariables();
        return Configuration.builder()
            .databaseCredentials(DatabaseCredentials.builder()
                .database(env.getString("mysql_db_name"))
                .host(env.getString("mysql_host"))
                .username(env.getString("mysql_user"))
                .password(env.getString("mysql_password"))
                .port(env.getInt("mysql_port"))
                .build())
            .mode(mode)
            .token(env.getString("discord_token_" + mode.name().toLowerCase()))
            .owmKey(env.getString("openweathermap_key"))
            .openAiKey(env.getString("open_ai_key"))
            .wordnikKey(env.getString("wordnik_key"))
            .build();
    }

}
