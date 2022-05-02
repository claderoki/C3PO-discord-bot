package com.c3po.helper;

import java.util.List;
import java.util.Random;

public class RandomHelper {
    public static <T> T choice(List<T> items) {
        Random rand = new Random();
        return items.get(rand.nextInt(items.size()));
    }
}
