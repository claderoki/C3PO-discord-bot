package com.c3po.helper.setting;

import com.c3po.connection.repository.SettingRepository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SettingValueLoader {

    public static HashMap<Integer, SettingValue> load(SettingScopeTarget target, String category) throws SQLException {
//        HashMap<Integer, SettingValue> values = SettingRepository.db().getSettingValues(target, category);
        HashMap<Integer, SettingValue> values = SettingCache.getValues(target, category);

        for (Map.Entry<String, Setting> entry: SettingCache.getSettings(category).entrySet()) {
            values.computeIfAbsent(entry.getValue().getId(), (c) -> SettingValue.builder()
                    .settingId(c)
                    .newValue(entry.getValue().getDefaultValue())
                    .target(target)
                    .build());
        }

        return values;
    }
}
