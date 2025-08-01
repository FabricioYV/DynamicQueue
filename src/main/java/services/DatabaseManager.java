package services;

import utils.ConfigManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private Connection connection;

    public boolean initialize() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            //Initial connection
            connect();

            // Create tables




            return true; // Retorna true si la conexión se establece correctamente
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Retorna false si hay un error al establecer la conexión
        }
    }
    private void connect() throws SQLException {
        String url = ConfigManager.DATABASE_URL;
        String username = ConfigManager.DATABASE_USERNAME;
        String password = ConfigManager.DATABASE_PASSWORD;

        connection = DriverManager.getConnection(url, username, password);

    }
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect(); // Reconectar automáticamente
        }
        return connection;
    }
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
// Fabricio YV 2025 | Project DynamicQueue for subject "ORIENTED TO OBJECT PROGRAMMING" at Universidad Nacional de Ingeneria | Computer Science
