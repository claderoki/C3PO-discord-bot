package com.c3po.command.profile;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZoneId;

@Getter
@Setter
public class Profile {
    private ZoneId timezone;
    private String pigeonName;
    private String country;
    private LocalDate dateOfBirth;
    private String city;
    private Long gold;
}