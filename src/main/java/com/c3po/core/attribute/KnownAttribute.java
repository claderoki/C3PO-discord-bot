package com.c3po.core.attribute;

import com.c3po.service.AttributeService;

public class KnownAttribute {
    public static final String CLOVERS = "clover";
    public final static String cloverKey = "clover";
    public final static int cloverId = AttributeService.getId(cloverKey);
    public final static String timezoneKey = "timezone";
    public final static int timezoneId = AttributeService.getId(timezoneKey);
    public final static String countryKey = "country";
    public final static int countryId = AttributeService.getId(countryKey);
    public final static String dateOfBirthKey = "date_of_birth";
    public final static int dateOfBirthId = AttributeService.getId(dateOfBirthKey);
    public final static String cityKey = "city";
    public final static int cityId = AttributeService.getId(cityKey);
}
