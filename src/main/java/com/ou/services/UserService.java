package com.ou.services;

import com.ou.pojos.DBUtils;
import com.ou.pojos.User;
import com.ou.utils.userbuilder.UserBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserService {
    public User createUser(UserBuilder userBuilder) {
        try (Connection conn = DBUtils.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement stm = userBuilder.build(conn);
            if (stm == null) {
                conn.rollback();
                return null;
            }
            conn.commit();
            return userBuilder.getUser();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
