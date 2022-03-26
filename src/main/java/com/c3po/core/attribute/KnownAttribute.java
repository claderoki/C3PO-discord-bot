package com.c3po.core.attribute;

import com.c3po.service.AttributeService;

public class KnownAttribute {
    public static final String CLOVERS = "clover";

    public static String cloverKey = "clover";
    public static int cloverId = AttributeService.getId(cloverKey);

    public static String timezoneKey = "timezone";
    public static int timezoneId = AttributeService.getId(timezoneKey);

    public static String countryKey = "country";
    public static int countryId = AttributeService.getId(countryKey);

    public static String dateOfBirthKey = "date_of_birth";
    public static int dateOfBirthId = AttributeService.getId(dateOfBirthKey);

    public static String cityKey = "city";
    public static int cityId = AttributeService.getId(cityKey);


}
