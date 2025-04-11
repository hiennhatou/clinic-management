package com.ou.clinicmanagement;

import static org.junit.jupiter.api.Assertions.*;

import com.ou.utils.secure.hash.SecurityHash;
import com.ou.utils.secure.hash.SecurityHashUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@DisplayName("Test Hashing Password")
public class TestSecurityHash {
    @Test
    @DisplayName("Test Hash Password")
    public void testHashPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecurityHash securityHash1 = SecurityHashUtils.hashPassword("Hien");
        SecurityHash securityHash2 = SecurityHashUtils.hashPassword("Hien");
        assertAll(
                () -> assertNotEquals(securityHash1.getHash(), securityHash2.getHash()),
                () -> assertNotEquals(securityHash1.getSalt(), securityHash2.getSalt())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"Hien", "Hien1", "HienNguyen", "hien", "HIEN"})
    @DisplayName("Test Verify Password")
    public void testVerifyPassword(String pass) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecurityHash securityHash = SecurityHashUtils.hashPassword("Hien");
        if (pass.equals("Hien")) {
            assertTrue(SecurityHashUtils.verifyPassword(pass, securityHash));
        } else {
            assertFalse(SecurityHashUtils.verifyPassword(pass, securityHash));
        }
    }
}
