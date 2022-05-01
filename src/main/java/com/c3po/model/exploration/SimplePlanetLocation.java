package com.c3po.model.exploration;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SimplePlanetLocation {
    private int id;
    private String imageUrl;
    private int travelDistance;
}
