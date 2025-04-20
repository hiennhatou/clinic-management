package com.ou.services;

import com.ou.pojos.DBUtils;
import com.ou.pojos.User;
import com.ou.utils.secure.hash.SecurityHash;
import com.ou.utils.secure.hash.SecurityHashUtils;
import com.ou.utils.validation.UserValidation;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    public List<User> getAllUsersByRole(String role) throws SQLException {
        List<User> users = new ArrayList<>();
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("select * from users where role=?");
            stm.setString(1, role);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUsername(rs.getString("username"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setMiddleName(rs.getString("middle_name"));
                user.setRole(rs.getString("role"));
                user.setId(rs.getLong("id"));
                users.add(user);
            }
            return users;
        }
    }


    public User getUserById(long id) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("select * from users where id=?");
            stm.setLong(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUsername(rs.getString("username"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setMiddleName(rs.getString("middle_name"));
                user.setRole(rs.getString("role"));
                user.setId(rs.getLong("id"));
                return user;
            }
            return null;
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        try (Connection conn = DBUtils.getConnection()) {
            ResultSet rs = conn.prepareStatement("select * from users").executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUsername(rs.getString("username"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setMiddleName(rs.getString("middle_name"));
                user.setRole(rs.getString("role"));
                user.setId(rs.getLong("id"));
                users.add(user);
            }
            return users;
        }
    }

    public User createUser(User user) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        try (Connection connection = DBUtils.getConnection()) {
            UserValidation.validate(user);
            SecurityHash hash = SecurityHashUtils.hashPassword(user.getPassword());
            user.setPassword(hash.getHash());
            user.setSalt(hash.getSalt());
            PreparedStatement stm = connection.prepareStatement("insert into users(username, password, salt, role, first_name, last_name, middle_name) values(?, ?, ?, ?, ?, ?, ?)");
            stm.setString(1, user.getUsername());
            stm.setString(2, user.getPassword());
            stm.setString(3, user.getSalt());
            stm.setString(4, user.getRole());
            stm.setString(5, user.getFirstName());
            stm.setString(6, user.getLastName());
            stm.setString(7, user.getMiddleName());
            stm.executeUpdate();
            return user;
        }
    }

    public String updateUsername(String username, long id) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            UserValidation.validateUsername(username);
            PreparedStatement pstm = conn.prepareStatement("update users set username=? where id=?");
            pstm.setString(1, username);
            pstm.setLong(2, id);
            pstm.executeUpdate();
            return username;
        }
    }

    public String updateFirstName(String firstName, long id) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            firstName = UserValidation.validateFirstName(firstName);
            PreparedStatement stm = conn.prepareStatement("update users set first_name=? where id=?");
            stm.setString(1, firstName);
            stm.setLong(2, id);
            stm.executeUpdate();
            return firstName;
        }
    }

    public String updateLastName(String lastName, long id) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("update users set last_name=? where id=?");
            stm.setString(1, lastName);
            stm.setLong(2, id);
            stm.executeUpdate();
            return lastName;
        }
    }

    public String updateMiddleName(String middleName, long id) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("update users set middle_name=? where id=?");
            stm.setString(1, middleName);
            stm.setLong(2, id);
            stm.executeUpdate();
            return middleName;
        }
    }

    public void updatePassword(String password, long id) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        try (Connection conn = DBUtils.getConnection()) {
            UserValidation.validatePassword(password);
            SecurityHash hash = SecurityHashUtils.hashPassword(password);
            PreparedStatement stm = conn.prepareStatement("update users set password=?, salt=? where id=?");
            stm.setString(1, hash.getHash());
            stm.setString(2, hash.getSalt());
            stm.setLong(3, id);
            stm.executeUpdate();
        }
    }

    public String updateRole(String role, long id) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            UserValidation.validateRole(role);
            PreparedStatement stm = conn.prepareStatement("update users set role=? where id=?");
            stm.setString(1, role);
            stm.setLong(2, id);
            stm.executeUpdate();
            return role;
        }
    }

    public void deleteUser(long id) throws SQLException {
        try (Connection conn = DBUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("delete from users where id=?");
            stm.setLong(1, id);
            stm.executeUpdate();
        }
    }
}
