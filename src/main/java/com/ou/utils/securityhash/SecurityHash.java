package com.ou.utils.securityhash;

import lombok.Getter;
import lombok.Setter;

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
