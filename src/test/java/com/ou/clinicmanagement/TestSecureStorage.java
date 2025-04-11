package com.ou.clinicmanagement;

import com.ou.utils.secure.storage.SecureStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test Secure Storage")
public class TestSecureStorage {
    @ParameterizedTest()
    @ValueSource(strings = {"Hello", "Clinic Management", "Open university", "OU"})
    void testSaveValue(String input) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        SecureStorage.store("test", input);
        assertEquals(input, SecureStorage.retrieve("test"));
    }

    @AfterEach()
    void remove() {
        SecureStorage.delete("test");
    }
}
