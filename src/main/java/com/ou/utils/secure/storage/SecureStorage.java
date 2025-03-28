package com.ou.utils.secure.storage;

import com.ou.Config;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.prefs.Preferences;

public class SecureStorage {
    private static final String PRE_KEY = "secure";
    private static final String SECRET_KEY = Config.SECRET_KEY;

    public static void store(String key, String value) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Preferences prefs = Preferences.userNodeForPackage(SecureStorage.class);
        prefs.put(PRE_KEY + key, encrypt(value));
    }

    public static String retrieve(String key) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Preferences prefs = Preferences.userNodeForPackage(SecureStorage.class);
        String encryptedData = prefs.get(PRE_KEY + key, null);
        return encryptedData == null ? null : decrypt(encryptedData);
    }

    private static String encrypt(String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec key = new SecretKeySpec(padKey(SECRET_KEY).getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    private static String decrypt(String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] original = cipher.doFinal(Base64.getDecoder().decode(data));
        return new String(original);
    }

    private static String padKey(String key) {
        int maxChars = 16;
        if (key == null) return " ".repeat(maxChars);
        if (key.length() > maxChars) return key.substring(0, maxChars);
        return String.format("%-" + maxChars + "s", key);
    }
}
