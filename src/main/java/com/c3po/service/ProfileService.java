package com.c3po.service;

import com.c3po.command.profile.MemberProfile;
import com.c3po.command.profile.Profile;
import com.c3po.connection.repository.AttributeRepository;
import com.c3po.connection.repository.HumanRepository;
import com.c3po.core.Scope;
import com.c3po.core.ScopeTarget;
import com.c3po.core.attribute.KnownAttribute;
import com.c3po.core.property.PropertyValue;
import com.c3po.model.pigeon.Pigeon;

import java.util.ArrayList;
import java.util.List;

public class ProfileService {
    private static final Integer[] editableAttributeIds = {
        KnownAttribute.countryId,
        KnownAttribute.cityId,
        KnownAttribute.dateOfBirthId,
        KnownAttribute.timezoneId
    };

    private static List<PropertyValue> getProfilePropertyValues(ScopeTarget target, Integer... attributeIds) {
        Scope scope = target.getScope();

        List<PropertyValue> values = new ArrayList<>(AttributeRepository.db().getHydratedPropertyValues(target, attributeIds).values());

        if (scope.equals(Scope.MEMBER)) {
            values.addAll(AttributeRepository.db().getHydratedPropertyValues(ScopeTarget.user(target.getUserId()), attributeIds).values());
        }
        return values;
    }

    public static List<PropertyValue> getEditableProfilePropertyValues(ScopeTarget target) {
        return getProfilePropertyValues(target, editableAttributeIds);
    }

    public static Profile getProfile(ScopeTarget target) {
        Scope scope = target.getScope();
        Profile profile;

        if (scope.equals(Scope.MEMBER)) {
            profile = new MemberProfile();
        } else {
            profile = new Profile();
        }

        for(PropertyValue value: getProfilePropertyValues(target)) {
            profile.set(value.getParentId(), value.getValue());
        }

        Integer humanId = HumanService.getHumanId(target.getUserId());
        Long gold = HumanRepository.db().getGold(humanId);
        profile.set("gold", gold.toString());
        Integer pigeonId = PigeonService.getCurrentId(humanId);
        if (pigeonId != null) {
            Pigeon pigeon = PigeonService.getPigeon(pigeonId);
            profile.set("pigeonName", pigeon.getName());
        }

        return profile;
    }

}
