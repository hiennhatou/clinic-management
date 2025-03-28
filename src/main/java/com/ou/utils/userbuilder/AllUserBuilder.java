package com.ou.utils.userbuilder;

import com.ou.pojos.User;
import com.ou.utils.secure.hash.SecurityHash;
import com.ou.utils.secure.hash.SecurityHashUtils;
import lombok.Getter;
import lombok.Setter;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;

@Getter
@Setter
public class AllUserBuilder implements UserBuilder {
    protected User user;

    public AllUserBuilder(User user) {
        this.user = user;
    }

    @Override
    public PreparedStatement build(Connection connection) throws SQLException {
        try {
            hashPassword(user.getPassword());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
        PreparedStatement statement = connection.prepareStatement("INSERT INTO users (first_name, last_name, middle_name, username, role, password, salt) values (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, user.getFirstName());
        statement.setString(2, user.getLastName());
        statement.setString(3, user.getMiddleName());
        statement.setString(4, user.getUsername());
        statement.setString(5, user.getRole());
        statement.setString(6, user.getPassword());
        statement.setString(7, user.getSalt());
        statement.executeUpdate();
        if (statement.getUpdateCount() != 1) return null;
        ResultSet resultSet = statement.getGeneratedKeys();
        resultSet.next();
        user.setId(resultSet.getLong(1));
        return statement;
    }

    private void hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecurityHash securityHash = SecurityHashUtils.hashPassword(password);
        user.setPassword(securityHash.getHash());
        user.setSalt(securityHash.getSalt());
    }
}
