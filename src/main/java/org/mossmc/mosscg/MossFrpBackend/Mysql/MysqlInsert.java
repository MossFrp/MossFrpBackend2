package org.mossmc.mosscg.MossFrpBackend.Mysql;

import com.alibaba.fastjson.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MysqlInsert {
    //sql参考："insert into users (password,username,email,level,silver) values (?,?,?,?,?)"
    public static void insert(String table, JSONObject data) throws SQLException {
        Connection connection = MysqlConnection.getPoolConnection();
        String sql = "insert into "+table+" (<names>) values (<args>)";
        StringBuilder names = new StringBuilder();
        StringBuilder args = new StringBuilder();
        Map<Integer,String> cacheMap = new HashMap<>();
        final boolean[] first = {true};
        AtomicInteger count = new AtomicInteger(0);

        data.forEach((name, arg) -> {
            if (!first[0]) {
                names.append(",");
                args.append(",");
            }
            count.getAndIncrement();
            first[0] = false;
            names.append(name);
            args.append("?");
            cacheMap.put(count.get(),arg.toString());
        });

        sql = sql.replace("<names>",names.toString());
        sql = sql.replace("<args>",args.toString());
        PreparedStatement runMysql = connection.prepareStatement(sql);

        for (Map.Entry<Integer, String> entry : cacheMap.entrySet()) {
            Integer key = entry.getKey();
            String value = entry.getValue();
            runMysql.setString(key, value);
        }

        runMysql.executeUpdate();
    }
}
