package com.ou.utils.userbuilder;

import com.ou.pojos.Patient;
import lombok.Setter;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Setter
public class PatientBuilder extends AllUserBuilder {
    public PatientBuilder(Patient user) {
        super(user);
    }

    @Override
    public PreparedStatement build (Connection connection) throws SQLException {
        PreparedStatement previous = super.build(connection);
        if (previous == null) return null;
        Patient patient = (Patient) super.user;
        PreparedStatement statement = connection.prepareStatement("INSERT INTO patients (id, birthday, id_code) values (?,?,?)");
        statement.setLong(1, super.user.getId());
        statement.setDate(2, Date.valueOf(patient.getBirthday()));
        statement.setString(3, patient.getIdCode());
        statement.executeUpdate();

        if (statement.getUpdateCount() != 1) return null;
        return statement;
    }
}
