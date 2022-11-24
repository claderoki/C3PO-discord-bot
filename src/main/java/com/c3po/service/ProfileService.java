package com.c3po.service;

import com.c3po.command.profile.MemberProfile;
import com.c3po.command.profile.Profile;
import com.c3po.connection.repository.AttributeRepository;
import com.c3po.connection.repository.HumanRepository;
import com.c3po.core.Scope;
import com.c3po.core.ScopeTarget;
import com.c3po.core.attribute.KnownAttribute;
import com.c3po.core.property.PropertyValue;
import com.c3po.helper.ValueFormatter;
import com.c3po.model.pigeon.Pigeon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final HumanService humanService;
    private final PigeonService pigeonService;
    private final AttributeService attributeService;
    private final AttributeRepository attributeRepository;
    private final HumanRepository humanRepository;

    private static final ArrayList<Integer> editableAttributeIds = new ArrayList<>();

    private ArrayList<Integer> getEditableAttributeIds() {
        if (editableAttributeIds.isEmpty()) {
            editableAttributeIds.addAll(Stream.of(
                    KnownAttribute.countryKey,
                    KnownAttribute.cityKey,
                    KnownAttribute.dateOfBirthKey,
                    KnownAttribute.timezoneKey
            ).map(attributeService::getId).toList());
        }
        return editableAttributeIds;
    }

    private List<PropertyValue> getProfilePropertyValues(ScopeTarget target, List<Integer> attributeIds) {
        Scope scope = target.getScope();

        List<PropertyValue> values = new ArrayList<>(attributeRepository.getHydratedPropertyValues(target, attributeIds).values());

        if (scope.equals(Scope.MEMBER)) {
            values.addAll(attributeRepository.getHydratedPropertyValues(ScopeTarget.user(target.getUserId()), attributeIds).values());
        }
        return values;
    }

    public List<PropertyValue> getEditableProfilePropertyValues(ScopeTarget target) {
        return getProfilePropertyValues(target, getEditableAttributeIds());
    }

    public Profile getProfile(ScopeTarget target) {
        Scope scope = target.getScope();
        Profile profile;

        if (scope.equals(Scope.MEMBER)) {
            profile = new MemberProfile();
        } else {
            profile = new Profile();
        }

        ValueFormatter formatter = new ValueFormatter();
        for(PropertyValue propertyValue: getProfilePropertyValues(target, List.of())) {
            int attributeId = propertyValue.getParentId();
            String value = propertyValue.getValue();

            if (attributeId == attributeService.getId(KnownAttribute.timezoneKey)) {
                if (value != null) {
                    profile.setTimezone(ZoneId.of(value));
                }
            } else if (attributeId == attributeService.getId(KnownAttribute.countryKey)) {
                profile.setCountry(value);
            } else if (attributeId == attributeService.getId(KnownAttribute.dateOfBirthKey)) {
                profile.setDateOfBirth(formatter.optDate(value));
            } else if (attributeId == attributeService.getId(KnownAttribute.cityKey)) {
                profile.setCity(value);
            } else if (attributeId == attributeService.getId(KnownAttribute.cloverKey) && profile instanceof MemberProfile memberProfile) {
                memberProfile.setClovers(formatter.optLong(value));
            }
        }

        Integer humanId = humanService.getHumanId(target.getUserId());
        Long gold = humanRepository.getGold(humanId);
        profile.setGold(gold);
        Integer pigeonId = pigeonService.getCurrentId(humanId);
        if (pigeonId != null) {
            Pigeon pigeon = pigeonService.getPigeon(pigeonId);
            profile.setPigeonName(pigeon.getName());
        }

        return profile;
    }

}
