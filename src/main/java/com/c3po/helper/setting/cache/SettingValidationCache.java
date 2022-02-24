package com.c3po.helper.setting.cache;

import com.c3po.connection.repository.SettingRepository;
import com.c3po.helper.TimedTrigger;
import com.c3po.helper.cache.OldCache;
import com.c3po.helper.LogHelper;
import com.c3po.helper.setting.validation.SettingValidation;

import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;

public class SettingValidationCache extends OldCache<SettingValidation> {
    private static HashMap<Integer, ArrayList<SettingValidation>> validations = new HashMap<>();
    private static final TimedTrigger refresh = new TimedTrigger(Duration.ofHours(1));

    public static HashMap<Integer, ArrayList<SettingValidation>> get() throws SQLException {
        refresh.check(() -> {
            try {
                validations = SettingRepository.db().getValidations();
            } catch (SQLException e) {
                LogHelper.logException(e);
            }
        });

        return validations;
    }

    public static void clear() {
        validations.clear();
    }

}
