package com.c3po.helper.setting;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SettingValueLoader {

    /**
     * @param target target
     * @param category category
     * @return A map of <SettingID, SettingValue> with default values hydrated.
     * @throws SQLException
     */
    public static HashMap<Integer, SettingValue> load(SettingScopeTarget target, String category) throws SQLException {
        HashMap<Integer, SettingValue> values = SettingValueCache.get(target, category);
        for (Map.Entry<String, Setting> entry: SettingCache.get(category).entrySet()) {
            values.computeIfAbsent(entry.getValue().getId(), (c) -> SettingValue.builder()
                    .settingId(c)
                    .newValue(entry.getValue().getDefaultValue())
                    .target(target)
                    .build());
        }

        return values;
    }
}
