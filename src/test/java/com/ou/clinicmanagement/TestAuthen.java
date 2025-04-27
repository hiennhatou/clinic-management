package com.ou.clinicmanagement;

import com.ou.pojos.DBUtils;
import com.ou.pojos.User;
import com.ou.services.AuthService;
import com.ou.services.PatientService;
import com.ou.services.UserService;
import com.ou.utils.exceptions.AuthFail;
import com.ou.utils.secure.storage.SecureStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test authentication")
public class TestAuthen {
    AuthService authService = new AuthService();
    static UserService userService = new UserService();

    @BeforeAll
    static void setUp() throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setMiddleName("D");
        user.setUsername("testdoctor");
        user.setPassword("Hello1234@");
        user.setRole("DOCTOR");
        userService.createUser(user);
    }

    @ParameterizedTest()
    @ValueSource(strings = {"Hello1234@", "hello1234@", "HELLO1234@", "1234qfwefq", "hELLO1234@", "Hello"})
    void testLogin(String password) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (password.equals("Hello1234@")) {
            assertAll(
                () -> assertTrue(authService.authenticate("testdoctor", password)),
                () -> assertEquals("testdoctor", SecureStorage.retrieve("username"))
            );
        } else
            assertThrows(AuthFail.class, () -> authService.authenticate("doctor", password));
    }

    @AfterEach()
    void logout() {
        SecureStorage.delete("username");
        try (Connection connection = DBUtils.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("delete from users where username = 'testdoctor'");
            stm.execute();
        } catch (SQLException e) {}
    }
}
