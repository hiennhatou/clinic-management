package com.ou.utils.validation;

import com.ou.pojos.Medicine;
import com.ou.utils.exceptions.ValidatorException;

public class MedicineValidation {
    public static void validate(Medicine medicine) {
        medicine.setName(validateMedicineName(medicine.getName()));
        medicine.setPrice(validateMedicinePrice(medicine.getPrice()));
        medicine.setUnit(validateMedicineUnit(medicine.getUnit()));
        medicine.setUseness(validateMedicineUseness(medicine.getUseness()));
    }

    public static String validateMedicineName(String medicineName) {
        if (medicineName == null || medicineName.isBlank())
            throw new ValidatorException("Tên thuốc không được trống", "name");
        return medicineName.trim();
    }

    public static long validateMedicinePrice(String medicinePrice) {
        if (medicinePrice == null || medicinePrice.isBlank())
            throw new ValidatorException("Giá thuốc không được trống", "price");
        try {
            return validateMedicinePrice(Long.parseLong(medicinePrice));
        } catch (NumberFormatException e) {
            throw new ValidatorException("Giá thuốc không hợp lệ", "price");
        }
    }

    public static long validateMedicinePrice(long price) throws NumberFormatException {
        if (price < 0)
            throw new NumberFormatException();
        return price;
    }

    public static String validateMedicineUnit(String medicineUnit) {
        if (medicineUnit == null || medicineUnit.isBlank())
            throw new ValidatorException("Đơn vị thuốc không được trống", "unit");
        return medicineUnit.trim();
    }

    public static String validateMedicineUseness(String medicineUseness) {
        if (medicineUseness == null || medicineUseness.isBlank())
            throw new ValidatorException("Công dụng của thuốc không được trống", "useness");
        return medicineUseness.trim();
    }
}
