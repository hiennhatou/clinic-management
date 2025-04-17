package com.ou.services;

import com.ou.pojos.DBUtils;
import com.ou.pojos.Patient;
import com.ou.utils.validation.PatientValidation;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PatientService {
    public List<Patient> getPatients() {
        List<Patient> patients = new ArrayList<>();
        try (Connection conn = DBUtils.getConnection()) {
            ResultSet rs = conn.prepareStatement("select * from patients").executeQuery();
            while (rs.next()) {
                Patient patient = new Patient();
                patient.setId(rs.getInt("id"));
                patient.setFirstName(rs.getString("first_name"));
                patient.setMiddleName(rs.getString("middle_name"));
                patient.setLastName(rs.getString("last_name"));
                patient.setBirthday(rs.getDate("birthday").toLocalDate());
                patient.setIdCode(rs.getString("id_code"));
                patients.add(patient);
            }

            return patients;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Patient insertPatient(Patient patient) throws SQLException {
        if (patient == null) return null;
        PatientValidation.validate(patient);
        try (Connection connection = DBUtils.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("insert into patients (id_code, first_name, middle_name, last_name, birthday) values (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, patient.getIdCode());
            stm.setString(2, patient.getFirstName());
            stm.setString(3, patient.getMiddleName());
            stm.setString(4, patient.getLastName());
            stm.setDate(5, java.sql.Date.valueOf(patient.getBirthday()));
            stm.executeUpdate();
            ResultSet keys = stm.getGeneratedKeys();
            keys.next();
            patient.setId(keys.getInt(1));
            return patient;
        }
    }

    public String updateFirstName(String firstName, long id) throws SQLException {
        firstName = PatientValidation.validateFirstName(firstName);
        try (Connection connection = DBUtils.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("update patients set first_name = ? where id = ?");
            stm.setString(1, firstName);
            stm.setLong(2, id);
            stm.executeUpdate();
            return firstName;
        }
    }

    public String updateMiddleName(String middleName, long id) throws SQLException {
        middleName = middleName == null ? "" : middleName.trim();
        try (Connection connection = DBUtils.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("update patients set middle_name = ? where id = ?");
            stm.setString(1, middleName);
            stm.setLong(2, id);
            stm.executeUpdate();
            return middleName;
        }
    }

    public String updateLastName(String lastName, long id) throws SQLException {
        lastName = PatientValidation.validateLastName(lastName);
        try (Connection connection = DBUtils.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("update patients set last_name = ? where id = ?");
            stm.setString(1, lastName);
            stm.setLong(2, id);
            stm.executeUpdate();
            return lastName;
        }
    }

    public LocalDate updateBirthday(LocalDate birthdayInLocalDate, long id) throws SQLException {
        LocalDate birthday = PatientValidation.validateBirthday(birthdayInLocalDate);
        try (Connection connection = DBUtils.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("update patients set birthday = ? where id = ?");
            stm.setDate(1, java.sql.Date.valueOf(birthday));
            stm.setLong(2, id);
            stm.executeUpdate();
            return birthday;
        }
    }

    public String updateIdCode(String idCode, long id) throws SQLException {
        idCode = PatientValidation.validateIdCode(idCode);
        try (Connection connection = DBUtils.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("update patients set id_code = ? where id = ?");
            stm.setString(1, idCode);
            stm.setLong(2, id);
            stm.executeUpdate();
            return idCode;
        }
    }
}
