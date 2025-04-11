package com.ou.utils.userbuilder;

import com.ou.pojos.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface UserBuilder {
    PreparedStatement build(Connection conn) throws SQLException;
    User getUser();
}
