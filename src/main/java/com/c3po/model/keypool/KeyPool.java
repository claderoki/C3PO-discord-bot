package com.c3po.model.keypool;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class KeyPool<K extends Key> {
    private final List<K> pool;
    private int index = 0;

    public String get() throws OutOfKeysException {
        K current = pool.get(index);
        if (current.isValid()) {
            return current.getValue();
        }
        K key = pool.stream()
            .filter(Key::isValid)
            .findFirst()
            .orElseThrow(OutOfKeysException::new);
        index = pool.indexOf(key);
        return key.getValue();
    }

    public void invalidateCurrent() {
        pool.get(index).setValid(false);
    }
}
