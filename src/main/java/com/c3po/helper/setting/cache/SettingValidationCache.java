package com.c3po.helper.setting.cache;

import com.c3po.connection.repository.SettingRepository;
import com.c3po.helper.TimedTrigger;
import com.c3po.helper.setting.validation.SettingValidation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;

public class SettingValidationCache {
    private static HashMap<Integer, ArrayList<SettingValidation>> validations = new HashMap<>();
    private static final TimedTrigger refresh = new TimedTrigger(Duration.ofHours(1));

    public static HashMap<Integer, ArrayList<SettingValidation>> get() {
        refresh.check(() -> validations = SettingRepository.db().getValidations());
        return validations;
    }

}
