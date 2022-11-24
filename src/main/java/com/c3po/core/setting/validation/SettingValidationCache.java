package com.c3po.core.setting.validation;

import com.c3po.connection.repository.SettingRepository;
import com.c3po.helper.TimedTrigger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class SettingValidationCache {
    private final SettingRepository settingRepository;

    private static HashMap<Integer, ArrayList<SettingValidation>> validations = new HashMap<>();
    private static final TimedTrigger refresh = new TimedTrigger(Duration.ofHours(1));

    public HashMap<Integer, ArrayList<SettingValidation>> get() {
        refresh.check(() -> validations = settingRepository.getValidations());
        return validations;
    }

}
