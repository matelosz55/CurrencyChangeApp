package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtil {

    private static final String HOSTNAME = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE = "workshop2";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    public static Connection getConnection() throws SQLException {
        return getConnection(DATABASE);
    }

    public static Connection getConnection(String database) throws SQLException {
        return DriverManager.getConnection(getUrl(HOSTNAME, PORT, database), DB_USER, DB_PASS);
    }

    public static String getUrl(String host, String port, String database) {
        String params = "useSSL=false&characterEncoding=utf8";
        String template = "jdbc:mysql://%s:%s/%s?%s";
        return String.format(template, host, port, database, params);
    }

    public static void main(String[] args) throws SQLException {
        try (Connection connection = getConnection()) {
            System.out.println(connection);
        }
    }
}

