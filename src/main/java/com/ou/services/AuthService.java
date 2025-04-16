package com.ou.services;

import com.ou.pojos.DBUtils;
import com.ou.pojos.User;
import com.ou.utils.exceptions.AuthFail;
import com.ou.utils.secure.hash.SecurityHash;
import com.ou.utils.secure.hash.SecurityHashUtils;
import com.ou.utils.secure.storage.SecureStorage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {
    public boolean authenticate(String username, String password) {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("select * from users where username=?");
            stm.setString(1, username);
            ResultSet rs = stm.executeQuery();
            if (!rs.next()) throw new AuthFail("Thông tin đăng nhập không hợp lệ");
            SecurityHash hash = new SecurityHash(rs.getString("password"), rs.getString("salt"));
            if (!SecurityHashUtils.verifyPassword(password, hash)) throw new AuthFail("Thông tin đăng nhập không hợp lệ");
            SecureStorage.store("username", rs.getString("username"));
            return true;
        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException |
                 IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    static public User getCurrentUser() {
        try {
            String username = SecureStorage.retrieve("username");
            if (username == null) return null;
            try (Connection conn = DBUtils.getConnection()) {
                PreparedStatement stm = conn.prepareStatement("select * from users where username=?");
                stm.setString(1, username);
                ResultSet rs = stm.executeQuery();
                if (!rs.next()) throw new RuntimeException("Not found user");
                User user = new User();
                user.setUsername(rs.getString("username"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setMiddleName(rs.getString("middle_name"));
                user.setRole(rs.getString("role"));
                user.setId(rs.getLong("id"));
                return user;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException |
                 InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    static public void logout() {
        SecureStorage.delete("username");
    }
}
