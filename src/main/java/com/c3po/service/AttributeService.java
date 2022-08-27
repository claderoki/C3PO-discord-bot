package com.c3po.service;

import com.c3po.connection.repository.AttributeRepository;
import com.c3po.core.ScopeTarget;
import com.c3po.core.property.PropertyValue;
import com.c3po.helper.cache.keys.AttributeCodeKey;
import com.c3po.helper.cache.keys.AttributeIdKey;
import com.c3po.helper.cache.keys.AttributeValueKey;
import com.c3po.helper.cache.CacheManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AttributeService extends Service {
    private final AttributeRepository attributeRepository = AttributeRepository.db();

    public PropertyValue getAttributeValue(ScopeTarget target, int attributeId) {
        AttributeValueKey key = new AttributeValueKey(target, attributeId);
        return CacheManager.get().computeIfAbsent(key, (k) -> {
            Optional<PropertyValue> possibleValue = attributeRepository.getHydratedPropertyValue(target, attributeId);
            return possibleValue.orElse(null);
        });
    }

    private void cacheIdAndCodes() {
        HashMap<String, Integer> identifiers = attributeRepository.getAttributeIdentifiers();
        for (Map.Entry<String, Integer> mapping: identifiers.entrySet()) {
            String code = mapping.getKey();
            Integer id = mapping.getValue();

            AttributeIdKey idKey = new AttributeIdKey(code);
            AttributeCodeKey codeKey = new AttributeCodeKey(id);

            CacheManager.get().set(idKey, id);
            CacheManager.get().set(codeKey, code);
        }
    }

    protected Integer getCachedId(String code) {
        AttributeIdKey key = new AttributeIdKey(code);
        return CacheManager.get().get(key);
    }

    public Integer getId(String code) {
        Integer id = getCachedId(code);
        if (id != null) {
            return id;
        }
        cacheIdAndCodes();
        return getCachedId(code);
    }

    protected String getCachedCode(Integer id) {
        AttributeCodeKey key = new AttributeCodeKey(id);
        return CacheManager.get().get(key);
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
