package org.mossmc.mosscg.MossFrpBackend.Mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MysqlGetResult {
    public static ResultSet getResultSet(String sql) throws SQLException {
        Connection connection = MysqlConnection.getPoolConnection();
        return connection.prepareStatement(sql).executeQuery();
    }

    public static ResultSet getResultSet(String sql,String arg1) throws SQLException {
        Connection connection = MysqlConnection.getPoolConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, arg1);
        return statement.executeQuery();
    }
    public static ResultSet getResultSet(String sql,String arg1,String arg2) throws SQLException {
        Connection connection = MysqlConnection.getPoolConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, arg1);
        statement.setString(2, arg2);
        return statement.executeQuery();
    }
}
