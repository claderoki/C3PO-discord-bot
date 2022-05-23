package com.c3po;

import com.c3po.core.C3PO;
import com.c3po.helper.LogHelper;
import com.c3po.helper.environment.Configuration;
import com.c3po.helper.environment.ConfigurationLoader;
import com.c3po.helper.environment.Mode;
import org.springframework.boot.builder.SpringApplicationBuilder;

public class Main {
    public static void main(String[] args) {
        try {
            MainArguments arguments = MainArguments.from(args);
            Mode mode = Mode.valueOf(arguments.getStringOr("mode", Mode.DEVELOPMENT.name()).toUpperCase());
            Configuration.initiate(ConfigurationLoader.load(mode));
            runWithoutSpring();
        } catch (Exception e) {
            LogHelper.log(e);
        }
    }

    private static void runWithSpring() {
        new SpringApplicationBuilder(C3PO.class)
            .build()
            .run();
    }

    private static void runWithoutSpring() {
        C3PO c3po = new C3PO();
        c3po.run();
    }

}
