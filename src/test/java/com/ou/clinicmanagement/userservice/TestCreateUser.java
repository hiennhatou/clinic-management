package com.ou.clinicmanagement.userservice;

import com.ou.pojos.DBUtils;
import com.ou.pojos.Patient;
import com.ou.pojos.User;
import com.ou.services.UserService;
import com.ou.utils.userbuilder.AllUserBuilder;
import com.ou.utils.userbuilder.UserBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.*;

@DisplayName("Test Create User")
public class TestCreateUser {
    UserService userService = new UserService();

    @AfterEach
    public void afterEach() {
        try (Connection connection = DBUtils.getConnection()) {
            connection.prepareStatement("delete from users where username = 'johndoe'").executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Test Create User")
    public void testCreateUser() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setMiddleName("Smith");
        user.setUsername("johndoe");
        user.setPassword("Pharmacist@12345678");
        user.setRole("ADMIN");
        UserBuilder userBuilder = new AllUserBuilder(user);
        User result = userService.createUser(userBuilder);
        System.out.println("1");
        System.out.println(result.getPassword());
        System.out.println(result.getSalt());
        Assertions.assertNotNull(result);
        try (Connection connection = DBUtils.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            stm.setString(1, user.getUsername());
            ResultSet rs = stm.executeQuery();
            rs.next();
            Assertions.assertEquals(user.getUsername(), rs.getString("username"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test Create Patient")
    public void testCreatePatient() {
        Patient patient = new Patient();
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setMiddleName("Smith");
        patient.setUsername("johndoe");
        patient.setPassword("Hello1234@");
        patient.setRole("PATIENT");
        patient.setIdCode("Doctor@1234567");
        patient.setBirthday(new Date(1072890000).toLocalDate());
        UserBuilder userBuilder = new AllUserBuilder(patient);
        User result = userService.createUser(userBuilder);
        System.out.println("2");
        System.out.println(result.getPassword());
        System.out.println(result.getSalt());
        Assertions.assertNotNull(result);
        try (Connection connection = DBUtils.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            stm.setString(1, patient.getUsername());
            ResultSet rs = stm.executeQuery();
            rs.next();
            Assertions.assertEquals(patient.getUsername(), rs.getString("username"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
