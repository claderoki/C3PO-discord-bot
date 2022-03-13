package com.c3po.core.setting;

import com.c3po.core.Scope;
import com.c3po.helper.DataType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Setting {
    private int id;
    private Scope scope;
    private DataType type;
    private String subtype;
    private String defaultValue;
    private String category;
    private String key;
    private String description;
}
