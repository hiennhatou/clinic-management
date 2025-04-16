package com.ou.services;

import com.ou.pojos.DBUtils;
import com.ou.pojos.Patient;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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
          }

          return patients;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
