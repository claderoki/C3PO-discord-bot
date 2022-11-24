package com.c3po.core.property;

import com.c3po.core.Scope;
import com.c3po.helper.DataType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Attribute {
    private int id;
    private String key;
    private Scope scope;
    private DataType type;
    private String defaultValue;
}
