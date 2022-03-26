package com.c3po.command.profile;

import com.c3po.core.attribute.KnownAttribute;
import com.c3po.helper.ValueParser;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class Profile implements ValueParser {
    public String optString(String value) {
        return value;
    }

    private String timezone;
    private String pigeonName;
    private String country;
    private LocalDate dateOfBirth;
    private String city;
    private Long gold;

    public void set(int attributeId, String value) {
        if (attributeId == KnownAttribute.timezoneId) {
            this.timezone = value;
        } else if (attributeId == KnownAttribute.countryId) {
            this.country = value;
        } else if (attributeId == KnownAttribute.dateOfBirthId) {
            this.dateOfBirth = optDate(value);
        } else if (attributeId == KnownAttribute.cityId) {
            this.city = value;
        }
    }

    public void set(String key, String value) {
        if ("gold".equals(key)) {
            this.gold = optLong(value);
        }
    }
}