package com.c3po.helper;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class RandomHelper {

    private static <T> int index(Collection<T> items) {
        Random rand = new Random();
        return rand.nextInt(items.size());
    }

    public static <T> T choice(List<T> items) {
        return items.get(index(items));
    }

    public static <T> T choice(Collection<T> items) {
        int index = index(items);
        return items.stream().toList().get(index);
    }

}
