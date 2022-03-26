package com.c3po.command.profile;

import com.c3po.core.attribute.KnownAttribute;
import lombok.Getter;

@Getter
public class MemberProfile extends Profile {
    private long clovers;

    public void set(int attributeId, String value) {
        if (attributeId == KnownAttribute.cloverId) {
            this.clovers = optLong(value);
        } else {
            super.set(attributeId, value);
        }
    }
}
