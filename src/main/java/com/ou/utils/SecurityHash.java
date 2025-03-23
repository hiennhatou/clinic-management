package com.ou.utils;

public class SecurityHash {
    private String hash;
    private String salt;

    public SecurityHash(String password, String salt) {
        this.hash = password;
        this.salt = salt;
    }

    public String getHash() {
        return hash;
    }

    public String getSalt() {
        return salt;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
