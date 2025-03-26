package com.ou.utils;

import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DatePickerConverter extends StringConverter<LocalDate> {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public String toString(LocalDate object) {
        try {
            return formatter.format(object);
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public LocalDate fromString(String string) {
        try {
            return LocalDate.parse(string, formatter);
        } catch (DateTimeParseException e) {
        }
        return null;
    }
}
