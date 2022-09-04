package com.c3po.service;

import com.c3po.connection.repository.AttributeRepository;
import com.c3po.core.ScopeTarget;
import com.c3po.core.property.PropertyValue;
import com.c3po.helper.cache.Cache;
import com.c3po.helper.cache.keys.AttributeCodeKey;
import com.c3po.helper.cache.keys.AttributeIdKey;
import com.c3po.helper.cache.keys.AttributeValueKey;
import com.c3po.helper.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@org.springframework.stereotype.Service
public class AttributeService {
    Cache cache = CacheManager.get("attributes");

    @Autowired
    private AttributeRepository attributeRepository;

    public PropertyValue getAttributeValue(ScopeTarget target, int attributeId) {
        AttributeValueKey key = new AttributeValueKey(target, attributeId);
        return cache.computeIfAbsent(key, (k) -> attributeRepository.getHydratedPropertyValue(target, attributeId).orElse(null));
    }

    private void cacheIdAndCodes() {
        HashMap<String, Integer> identifiers = attributeRepository.getAttributeIdentifiers();
        for (Map.Entry<String, Integer> mapping: identifiers.entrySet()) {
            String code = mapping.getKey();
            Integer id = mapping.getValue();

            AttributeIdKey idKey = new AttributeIdKey(code);
            AttributeCodeKey codeKey = new AttributeCodeKey(id);

            cache.set(idKey, id);
            cache.set(codeKey, code);
        }
    }

    protected Integer getCachedId(String code) {
        AttributeIdKey key = new AttributeIdKey(code);
        return cache.get(key);
    }

    protected List<Integer> getCachedIds(String... codes) {
        return Arrays.stream(codes)
            .map(AttributeIdKey::new)
            .map(k -> cache.get(k))
            .toList();
    }

    public int getId(String code) {
        Integer id = getCachedId(code);
        if (id != null) {
            return id;
        }
        cacheIdAndCodes();
        return getCachedId(code);
    }

    public List<Integer> getIds(String... codes) {
        List<Integer> id = getCachedIds(codes);
        if (id.stream().anyMatch(Objects::isNull)) {
            cacheIdAndCodes();
        }
        return getCachedIds(codes);
    }

    protected String getCachedCode(Integer id) {
        AttributeCodeKey key = new AttributeCodeKey(id);
        return cache.get(key);
    }

    public String getCode(Integer id) {
        String code = getCachedCode(id);
        if (code != null) {
            return code;
        }
        cacheIdAndCodes();
        return getCode(id);
    }

}
