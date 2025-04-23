package com.ou.utils.validation;

import com.ou.pojos.MedicineIngredient;
import com.ou.utils.exceptions.ValidatorException;

public class MedicineIngredientValidation {
    public static MedicineIngredient validate(MedicineIngredient medicineIngredient) {
        medicineIngredient.setUnit(validateUnit(medicineIngredient.getUnit()));
        medicineIngredient.setQuantity(validateQuantity(medicineIngredient.getQuantity()));
        return medicineIngredient;
    }

    public static String validateUnit(String unit) {
        if (unit == null || unit.isBlank())
            throw new ValidatorException("Đơn vị của thành phần không được trống", "unit");
        return unit;
    }

    public static double validateQuantity(double quantity) {
        if (quantity < 0.0)
            throw new ValidatorException("Lượng thành phần không hợp lệ", "quantity");
        return quantity;
    }
}
