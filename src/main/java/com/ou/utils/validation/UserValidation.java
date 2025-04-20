package com.ou.utils.validation;

import com.ou.pojos.User;
import com.ou.utils.exceptions.ValidatorException;

import java.util.Arrays;
import java.util.regex.Pattern;

public class UserValidation {
    public static void validate(User user) {
        user.setFirstName(validateFirstName(user.getFirstName()));
        user.setUsername(validateUsername(user.getUsername()));
        user.setPassword(validatePassword(user.getPassword()));
        user.setRole(validateRole(user.getRole()));
    }

    static public String validatePassword(String password) {
        if (password == null || password.length() <= 8)
            throw new ValidatorException("Mật khẩu phải dài từ 8 ký tự", "password");

        if (Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+{};:,<.>]).{8,}$").matcher(password).matches())
            return password;
        else
            throw new ValidatorException("Mật khẩu phải chứa ít nhất 1 ký tự hoa, 1 ký tự thường, 1 ký tự đặc biệt, 1 chữ số và có độ dài từ 8 ký tự.", "password");
    }

    static public String validateUsername(String username) {
        if (username == null || username.isBlank())
            throw new ValidatorException("Tên đăng nhập phải dài ít nhất 4 ký tự", "username");

        username = username.trim();

        if (Pattern.compile("^[a-zA-Z0-9_]{4,}$").matcher(username).matches())
            return username;
        else
            throw new ValidatorException("Tên đăng nhập chỉ chứa các ký tự hoa - thường, ký tự gạch chân và dài ít nhất 4 ký tự", "username");
    }

    static public String validateRole(String role) {
        if (role == null || !Arrays.asList(new String[]{"ADMIN", "DOCTOR", "PHARMACIST", "STAFF"}).contains(role))
            throw new ValidatorException("Vai trò không hợp lệ", "role");
        return role;
    }

    static public String validateFirstName(String firstName) {
        if (firstName == null || firstName.isBlank())
            throw new ValidatorException("Tên không được trống", "firstName");
        return firstName.trim();
    }
}
