package com.c3po;

import com.c3po.core.C3PO;
import com.c3po.helper.LogHelper;
import com.c3po.helper.environment.Configuration;
import com.c3po.helper.environment.ConfigurationLoader;
import com.c3po.helper.environment.Mode;

public class Main {
    public static void main(String[] args) {
        try {
            MainArguments arguments = MainArguments.from(args);
            Mode mode = Mode.valueOf(arguments.getStringOr("mode", Mode.DEVELOPMENT.name()).toUpperCase());
            Configuration.initiate(ConfigurationLoader.load(mode));
            C3PO bot = new C3PO(mode);
            bot.run();
        } catch (Exception e) {
            LogHelper.log(e);
        }
    }
}
