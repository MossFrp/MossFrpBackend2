package org.mossmc.mosscg.MossFrpBackend.Mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mossmc.mosscg.MossFrpBackend.BasicInfo.getConfig;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class MysqlConnection {
    public static int poolMax = 10;
    public static List<Connection> connectionPool = new ArrayList<>();

    public static void updatePoolConnection() {
        sendInfo("正在更新数据库连接！");
        List<Connection> newConnPool = new ArrayList<>();
        for (int i = 0;i<poolMax;i++) {
            newConnPool.add(getConnectionSilent());
        }
        List<Connection> oldConnPool = new ArrayList<>(connectionPool);
        connectionPool.clear();
        connectionPool = new ArrayList<>(newConnPool);
        oldConnPool.forEach(conn -> {
            try {
                conn.close();
            } catch (Exception e) {
                sendException(e);
            }
        });
        sendInfo("数据库连接更新完成！");
    }

    public static Random random = new Random();

    @SuppressWarnings("BusyWait")
    public static Connection getPoolConnection() {
        long sleepTime = 200;
        long maxTime = 1000;
        while (true) {
            try {
                Connection result = connectionPool.get(random.nextInt(poolMax));
                if (!result.isClosed()) {
                    sleepTime = 0;
                    return result;
                }
            } catch (Exception e) {
                sendException(e);
            } finally {
                try {
                    Thread.sleep(sleepTime);
                    if (sleepTime < maxTime) {
                        sleepTime+=100;
                    }
                } catch (InterruptedException e) {
                    sendException(e);
                }
            }
        }
    }

    public static Connection getConnectionSilent() {
        Connection cacheConnection = null;
        boolean connected = false;
        int times = 0;
        String sqlAddress = "jdbc:mysql://"+getConfig("mysqlAddress")+":"+getConfig("mysqlPort")+"/"+getConfig("mysqlDatabase")+"?autoReconnect=true&characterEncoding=utf8";
        while (times <= 2 && !connected) {
            times++;
            try {
                cacheConnection = DriverManager.getConnection(sqlAddress, getConfig("mysqlUsername"), getConfig("mysqlPassword"));
                connected = true;
            } catch (Exception e) {
                sendException(e);
            }
        }
        return cacheConnection;
    }

    /*
    public static Connection connection;

    public static void updateConnection() {
        Connection newConnection = getConnection();
        try {
            connection.close();
        } catch (Exception e) {
            sendException(e);
        }
        connection = newConnection;
    }

    public static Connection getConnection() {
        Connection cacheConnection = null;
        boolean connected = false;
        int times = 0;
        String sqlAddress = "jdbc:mysql://"+getConfig("mysqlAddress")+":"+getConfig("mysqlPort")+"/"+getConfig("mysqlDatabase")+"?autoReconnect=true&characterEncoding=utf8";
        while (times <= 2 && !connected) {
            times++;
            sendInfo("正在连接Mysql数据库，第"+times+"次尝试");
            sendInfo("数据库地址："+sqlAddress);
            try {
                cacheConnection = DriverManager.getConnection(sqlAddress, getConfig("mysqlUsername"), getConfig("mysqlPassword"));
                connected = true;
                sendInfo("第"+times+"次连接成功！");
            } catch (Exception e) {
                sendException(e);
            }
        }
        return cacheConnection;
    }

     */
}
