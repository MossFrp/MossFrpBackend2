package org.mossmc.mosscg.MossFrpBackend.Mysql;

import java.sql.Connection;
import java.sql.SQLException;

public class MysqlUpdate {
    public static void execute(String sql) throws SQLException {
        Connection connection = MysqlConnection.getPoolConnection();
        connection.prepareStatement(sql).executeUpdate();
    }
}
