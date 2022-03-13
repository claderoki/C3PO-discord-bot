package com.c3po.service;

import com.c3po.connection.repository.AttributeRepository;
import com.c3po.core.ScopeTarget;
import com.c3po.core.property.PropertyValue;
import com.c3po.helper.cache.Cache;
import com.c3po.helper.cache.keys.AttributeCodeKey;
import com.c3po.helper.cache.keys.AttributeIdKey;
import com.c3po.helper.cache.keys.AttributeValueKey;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AttributeService extends Service {

    public static PropertyValue getAttributeValue(ScopeTarget target, int attributeId) {
        AttributeValueKey key = new AttributeValueKey(target, attributeId);
        PropertyValue value = Cache.get(key);
        if (value != null) {
            return value;
        }

        Optional<PropertyValue> possibleValue = AttributeRepository.db().getHydratedPropertyValue(target, attributeId);
        value = possibleValue.orElse(null);

        if (value != null) {
            Cache.set(key, value);
            return value;
        }

        return null;
    }

    private static void cacheIdAndCodes() {
        HashMap<String, Integer> identifiers = AttributeRepository.db().getAttributeIdentifiers();
        for (Map.Entry<String, Integer> mapping: identifiers.entrySet()) {
            String code = mapping.getKey();
            Integer id = mapping.getValue();

            AttributeIdKey idKey = new AttributeIdKey(code);
            AttributeCodeKey codeKey = new AttributeCodeKey(id);

            Cache.set(idKey, id);
            Cache.set(codeKey, code);
        }
    }

    protected static Integer getCachedId(String code) {
        AttributeIdKey key = new AttributeIdKey(code);
        return Cache.get(key);
    }

    public static Integer getId(String code) {
        Integer id = getCachedId(code);
        if (id != null) {
            return id;
        }
        cacheIdAndCodes();
        return getCachedId(code);
    }

    protected static String getCachedCode(Integer id) {
        AttributeCodeKey key = new AttributeCodeKey(id);
        return Cache.get(key);
    }

    public static String getCode(Integer id) {
        String code = getCachedCode(id);
        if (code != null) {
            return code;
        }
        cacheIdAndCodes();
        return getCode(id);
    }

}
