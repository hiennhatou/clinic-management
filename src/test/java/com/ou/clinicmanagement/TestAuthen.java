package com.ou.clinicmanagement;

import com.ou.services.AuthService;
import com.ou.utils.exceptions.AuthFail;
import com.ou.utils.secure.storage.SecureStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test authentication")
public class TestAuthen {
    AuthService authService = new AuthService();

    @ParameterizedTest()
    @ValueSource(strings = {"Hello1234@", "hello1234@", "HELLO1234@", "1234qfwefq", "hELLO1234@", "Hello"})
    void testLogin(String password) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (password.equals("Hello1234@")) {
            assertAll(
                () -> assertTrue(authService.authenticate("doctor", password)),
                () -> assertEquals("doctor", SecureStorage.retrieve("username"))
            );
        } else
            assertThrows(AuthFail.class, () -> authService.authenticate("doctor", password));
    }

    @AfterEach()
    void logout() {
        SecureStorage.delete("username");
    }
}
