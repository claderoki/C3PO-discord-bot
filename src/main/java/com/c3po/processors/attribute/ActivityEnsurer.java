package com.c3po.processors.attribute;

import com.c3po.connection.repository.AttributeRepository;
import com.c3po.core.attribute.KnownAttribute;
import com.c3po.helper.DateTimeHelper;
import com.c3po.service.AttributeService;
import discord4j.core.object.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class ActivityEnsurer extends AttributeEnsurer {
    public ActivityEnsurer(AttributeRepository attributeRepository, AttributeService attributeService) {
        super(attributeRepository, attributeService);
    }

    @Override
    protected String getAttributeCode() {
        return KnownAttribute.lastActive;
    }

    @Override
    protected String getValue(Member member) {
        return DateTimeHelper.now().format(DateTimeHelper.DATETIME_FORMATTER);
    }
}
