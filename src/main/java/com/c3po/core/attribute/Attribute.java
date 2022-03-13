package com.c3po.core.attribute;

import com.c3po.core.Scope;
import com.c3po.helper.DataType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Attribute {
    private int id;
    private Scope scope;
    private DataType type;
    private String subtype;
    private String key;
}
