package com.c3po.connection.repository;

import com.c3po.connection.Repository;
import com.c3po.database.StringParameter;
import com.c3po.model.keypool.Key;
import com.c3po.model.keypool.KeyPool;
import org.springframework.stereotype.Service;

@Service
public class KeyPoolRepository extends Repository {
    public synchronized KeyPool<Key> getKeyPool(String type) {
        return new KeyPool<>(getMany("SELECT * FROM key_pool WHERE type = ?", new StringParameter(type))
            .stream()
            .map(r -> new Key(r.getBool("valid"), r.getString("value")))
            .toList());
    }

    public synchronized void invalidateKey(String type, String value) {
        execute("UPDATE key_pool SET valid = 0 WHERE type = ? AND value = ?", new StringParameter(type), new StringParameter(value));
    }

}