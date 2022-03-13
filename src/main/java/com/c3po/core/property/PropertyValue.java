package com.c3po.core.property;

import com.c3po.core.DataFormatter;
import com.c3po.core.ScopeTarget;
import com.c3po.helper.DataType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PropertyValue {
    private Integer id;
    private Integer parentId;
    private ScopeTarget target;
    private DataType type;
    private String value;
    private String newValue;

    public void setValue(String value) {
        if (this.value == null) {
            this.value = this.newValue;
        }
        this.newValue = value;
    }

    public String getValue() {
        return newValue != null ? newValue : value;
    }

    public String getOriginalValue() {
        return value;
    }

    public boolean changed() {
        return newValue != null && !newValue.equals(value);
    }

    public <T> T getParsedValue() {
        return (T)DataFormatter.parse(type, getValue());
    }

    /**
     * Only for numeric values
     */
    public void increment(Long amount) {
        if (type.equals(DataType.INTEGER)) {
            long value = getParsedValue();
            setValue(String.valueOf(value + amount));
        }
    }

    public void increment(Integer amount) {
        if (type.equals(DataType.INTEGER)) {
            long value = getParsedValue();
            setValue(String.valueOf(value + amount));
        }
    }

}
