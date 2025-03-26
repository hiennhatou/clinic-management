package com.ou.utils.securityhash;

import com.ou.utils.HexUtils;
import lombok.Getter;
import lombok.Setter;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

@Setter
@Getter
public class SecurityHash {
    private String hash;
    private String salt;

    public SecurityHash(String password, String salt) {
        this.hash = password;
        this.salt = salt;
    }
}
