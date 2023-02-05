package com.c3po.core.command;

import com.c3po.helper.EventHelper;
import com.c3po.helper.cache.CacheManager;
import com.c3po.helper.cache.keys.BucketKey;


public class BucketManager {
    private final Bucket bucket;
    private final BucketData bucketData;

    public BucketManager(Bucket bucket, Command command, Context context) {
        this.bucket = bucket;
        BucketKey key = new BucketKey(EventHelper.scopeToTarget(bucket.getPer(), context.getEvent()), command.getName());
        this.bucketData = CacheManager.get().computeIfAbsent(key, k -> new BucketData());
    }

    public void before() {
        bucketData.incrementAmount();
    }

    public void after() {
        bucketData.decrementAmount();
    }

    public boolean validate() {
        if (bucketData == null) {
            return true;
        }

        if (bucketData.getAmount() >= bucket.getAmount()) {
            return false;
        }

        return true;
    }
}
