package jp.naist.sdlab.miku.module.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DatabaseManager {
    public static final String ip = "jdbc:mysql://127.0.0.1";
    public static final String port = "3306";
    public static final String db = "satd_replace_db";
    public Connection connection;
    abstract void initDB() throws SQLException;

    public DatabaseManager() throws SQLException {
        this(this.db);
    }
    public DatabaseManager(String db) throws SQLException {
        connection = DriverManager.getConnection(
                ip+":"+port+"/"+db,
                "me",
                "goma");
    }
}
