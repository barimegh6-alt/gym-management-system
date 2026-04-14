package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/gym_management"
                                         + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root123"; // ← change to your MySQL password

    private static Connection connection = null;

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || !connection.isValid(2)) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("[DB] Connected successfully.");
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL driver not found. Add mysql-connector-j jar to lib/", e);
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && connection.isValid(1)) { connection.close(); connection = null; }
        } catch (SQLException e) { System.err.println("[DB] Close error: " + e.getMessage()); }
    }
}
