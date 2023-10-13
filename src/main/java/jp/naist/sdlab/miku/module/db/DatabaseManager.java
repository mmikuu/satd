package jp.naist.sdlab.miku.module.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    public static final String ip = "jdbc:mysql://127.0.0.1";
    public static final String port = "3306";
    public static final String db = "satd_replace_db";
    public Connection connection;

    public DatabaseManager() throws SQLException {
        connection = DriverManager.getConnection(
                ip+":"+port+"/"+db,
                "me",
                "goma");
    }
}
