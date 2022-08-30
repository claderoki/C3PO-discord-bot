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
        return items.stream().toList().get(index(items));
    }

    public static String generateString(int limit) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();
        return random.ints(leftLimit, rightLimit + 1)
            .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
            .limit(limit)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    }
}
