package carsharing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {

    private String JDBCDriver;
    private String DBUrl;

    public MyConnection(String JDBCDriver, String DBUrl) {
        this.JDBCDriver = JDBCDriver;
        this.DBUrl = DBUrl;
    }

    public Connection getConnection() {
        try {
            Class.forName(JDBCDriver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        Connection conn;
        System.out.println("Connecting to database...");
        try {
            conn = DriverManager.getConnection(DBUrl);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Connected to database...");

        return conn;
    }
}