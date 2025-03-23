package com.ou.clinicmanagement;

import com.ou.services.UserService;

import static org.junit.jupiter.api.Assertions.*;

import com.ou.utils.SecurityHash;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@DisplayName("Test User Service")
public class UserServiceTest {
    private final UserService userService = new UserService();

    @Test
    @DisplayName("Test Hash Password")
    public void testHashPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecurityHash securityHash1 = userService.hashPassword("Hien");
        SecurityHash securityHash2 = userService.hashPassword("Hien");
        assertAll(
                () -> assertNotEquals(securityHash1.getHash(), securityHash2.getHash()),
                () -> assertNotEquals(securityHash1.getSalt(), securityHash2.getSalt())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"Hien", "Hien1", "HienNguyen", "hien", "HIEN"})
    @DisplayName("Test Verify Password")
    public void testVerifyPassword(String pass) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecurityHash securityHash = userService.hashPassword("Hien");
        if (pass.equals("Hien")) {
            assertTrue(userService.verifyPassword(pass, securityHash));
        } else {
            assertFalse(userService.verifyPassword(pass, securityHash));
        }
    }
}
