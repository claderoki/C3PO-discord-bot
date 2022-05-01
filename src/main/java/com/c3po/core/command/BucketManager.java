package com.c3po.core.command;

import com.c3po.helper.EventHelper;

import java.util.HashMap;
import java.util.Map;


public class BucketManager {
    private final static Map<String, BucketData> data = new HashMap<>();

    private final Bucket bucket;
    private final String key;

    public BucketManager(Bucket bucket, Command command, Context context) {
        this.bucket = bucket;
        this.key =  EventHelper.scopeToTarget(bucket.getPer(), context.getEvent()) +":"+ command.getName();
    }

    public void before() {
        data.computeIfAbsent(key, (c) -> new BucketData()).incrementAmount();
    }

    public void after() {
        data.computeIfAbsent(key, (c) -> new BucketData()).decrementAmount();
    }

    public boolean validate() {
        BucketData bucketData = data.get(key);
        if (bucketData == null) {
            return true;
        }

        if (bucketData.getAmount() >= bucket.getAmount()) {
            return false;
        }

        return true;
    }
}
