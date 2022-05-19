package com.c3po.core.attribute;

import com.c3po.service.AttributeService;

public class KnownAttribute {
    private static final AttributeService attributeService = new AttributeService();

    public static final String CLOVERS = "clover";
    public final static String cloverKey = "clover";
    public final static int cloverId = attributeService.getId(cloverKey);
    public final static String timezoneKey = "timezone";
    public final static int timezoneId = attributeService.getId(timezoneKey);
    public final static String countryKey = "country";
    public final static int countryId = attributeService.getId(countryKey);
    public final static String dateOfBirthKey = "date_of_birth";
    public final static int dateOfBirthId = attributeService.getId(dateOfBirthKey);
    public final static String cityKey = "city";
    public final static int cityId = attributeService.getId(cityKey);
}
