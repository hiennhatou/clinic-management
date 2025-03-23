package com.ou.services;

import com.ou.utils.HexUtils;
import com.ou.utils.SecurityHash;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

public class UserService {
    public SecurityHash hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecureRandom random = new SecureRandom();
        byte[] saltInBytes = new byte[16];
        random.nextBytes(saltInBytes);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), saltInBytes, 65536, 256);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        String hash = HexUtils.toHexString(keyFactory.generateSecret(spec).getEncoded());

        String salt = HexUtils.toHexString(saltInBytes);

        return new SecurityHash(hash, salt);
    }

    public boolean verifyPassword(String password, SecurityHash securityHash) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] saltInBytes = HexUtils.fromHexString(securityHash.getSalt());

        KeySpec spec = new PBEKeySpec(password.toCharArray(), saltInBytes, 65536, 256);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return Arrays.equals(keyFactory.generateSecret(spec).getEncoded(), HexUtils.fromHexString(securityHash.getHash()));
    }
}
