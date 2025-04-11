package com.ou.utils.exceptions;

public class AuthFail extends RuntimeException {
    public AuthFail(String message) {
        super(message);
    }
}
