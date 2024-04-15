package org.mossmc.mosscg.MossFrpBackend.Mysql;

import java.sql.Connection;
import java.sql.SQLException;

public class MysqlDelete {
    public static void execute(String table, String key, String value) throws SQLException {
        Connection connection = MysqlConnection.getPoolConnection();
        String sql = "delete from "+table+" where "+key+"='"+value+"'";
        connection.prepareStatement(sql).executeUpdate();
    }
}
