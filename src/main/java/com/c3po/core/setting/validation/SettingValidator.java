package com.c3po.core.setting.validation;

import com.c3po.helper.DataType;
import com.c3po.core.property.PropertyValue;
import com.c3po.service.SettingService;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingValidator {
    private final ArrayList<SettingValidation> validations;
    private final HashMap<Integer, PropertyValue> settingValues;

    public SettingValidator(ArrayList<SettingValidation> validations, HashMap<Integer, PropertyValue> settingValues) {
        this.validations = validations;
        this.settingValues = settingValues;
    }

    private boolean compareValues(DataType dataType, String left, String right, Condition condition) {
        if (dataType.equals(DataType.INTEGER)) {
            return numericComparison(Float.parseFloat(left), Float.parseFloat(right), condition);
        }
        return false;
    }

    private boolean numericComparison(Float left, Float right, Condition condition) {
        return switch (condition) {
            case GT -> left > right;
            case LT -> left < right;
            case GTE -> left >= right;
            case LTE -> left <= right;
            default -> false;
        };
    }

    public SettingValidationResult validate() {
        SettingValidationResult result = new SettingValidationResult();
        for (SettingValidation validation: validations) {
            if (validation.getValueType().equals(ValueType.SETTING)) {
                PropertyValue left = settingValues.get(validation.getSettingId());
                PropertyValue right = settingValues.get(Integer.parseInt(validation.getValue()));
                if (!compareValues(left.getType(), left.getValue(), right.getValue(), validation.getCondition())) {
                    String error = SettingValidationFormatter.getError(
                            left.getParentId().toString(),
                            right.getParentId().toString(),
                            validation.getCondition());
                    result.addError(error);
                }
            }
        }
        return result;
    }
}
