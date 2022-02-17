package com.c3po.helper.setting;

import com.c3po.connection.repository.SettingRepository;

import java.sql.SQLException;
import java.util.HashMap;

public class SettingValueLoader {

    public static HashMap<Integer, SettingValue> load(SettingScopeTarget target, String category) throws SQLException {
        HashMap<Integer, SettingValue> values = SettingRepository.db().getSettingValues(target, category);

        for (Setting setting: SettingCache.getSettings(category)) {
            values.computeIfAbsent(setting.getId(), (c) -> SettingValue.builder()
                    .settingId(c)
                    .newValue(setting.getDefaultValue())
                    .target(target)
                    .build());
        }

        return values;
    }
}
