package com.ou.utils.validation;

import com.ou.pojos.Patient;
import com.ou.utils.exceptions.ValidatorException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class PatientValidation {
    public static Patient validate(Patient patient) {
        if (patient == null) return null;
        patient.setFirstName(validateFirstName(patient.getFirstName()));
        patient.setLastName(validateLastName(patient.getLastName()));
        patient.setMiddleName(patient.getMiddleName() == null ? "" : patient.getMiddleName().trim());
        patient.setIdCode(validateIdCode(patient.getIdCode()));
        patient.setBirthday(validateBirthday(patient.getBirthday()));
        return patient;
    }

    public static String validateFirstName(String firstName) {
        if (firstName == null || firstName.isBlank())
            throw new ValidatorException("Tên không được bỏ trống", "firstName");
        return firstName.trim();
    }

    public static String validateLastName(String lastName) {
        if (lastName == null || lastName.isBlank())
            throw new ValidatorException("Họ không được bỏ trống", "lastName");
        return lastName.trim();
    }

    public static String validateIdCode(String idCode) {
        if (idCode == null || idCode.isBlank() || !Pattern.matches("^[0-9]{12,12}$", idCode.trim()))
            throw new ValidatorException("Số CCCD không hợp lệ", "idCode");
        return idCode.trim();
    }

    public static LocalDate validateBirthday(LocalDate birthDate) {
        if (birthDate == null)
            throw new ValidatorException("Ngày sinh không hợp lệ", "birthday");
        return birthDate;
    }
}
