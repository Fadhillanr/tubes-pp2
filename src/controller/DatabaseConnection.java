// package controller;

// import java.sql.*;

// public class DatabaseConnection {
//     private static final String DB_URL = "jdbc:mysql://localhost:3306/ewaste_db";
//     private static final String USER = "root";
//     private static final String PASS = "";
//     private static Connection conn = null;
    
//     public static Connection getConnection() {
//         try {
//             if (conn == null || conn.isClosed()) {
//                 Class.forName("com.mysql.cj.jdbc.Driver");
//                 conn = DriverManager.getConnection(DB_URL, USER, PASS);
//             }
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//         return conn;
//     }
// }
package controller;

import javax.swing.*;
import java.sql.*;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ewaste_db";
    private static final String USER = "root";
    private static final String PASS = "";
    private static Connection conn = null;
    
    public static Connection getConnection() {
        try {
            // Check if connection is null or closed
            if (conn == null || conn.isClosed()) {
                // Register JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Try to connect to database
                try {
                    // First try to connect to existing database
                    conn = DriverManager.getConnection(DB_URL, USER, PASS);
                } catch (SQLException e) {
                    // If database doesn't exist, create it
                    createDatabase();
                    // Then try to connect again
                    conn = DriverManager.getConnection(DB_URL, USER, PASS);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Database Error: " + e.getMessage() + "\n" +
                "Please check:\n" +
                "1. MySQL server is running\n" +
                "2. Username and password are correct\n" +
                "3. Database 'ewaste_db' exists",
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
        return conn;
    }
    
    private static void createDatabase() {
        try {
            // Connect to MySQL server without selecting a database
            Connection tempConn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306",
                USER,
                PASS
            );
            
            // Create database if it doesn't exist
            Statement stmt = tempConn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS ewaste_db");
            
            // Create tables
            stmt.executeUpdate("USE ewaste_db");
            
            // Create users table
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "username VARCHAR(50) UNIQUE NOT NULL, " +
                "password VARCHAR(255) NOT NULL, " +
                "email VARCHAR(100) UNIQUE NOT NULL, " +
                "phone_number VARCHAR(15) UNIQUE NOT NULL, " +
                "address TEXT, " +
                "role ENUM('ADMIN', 'STAFF', 'USER') DEFAULT 'USER', " +
                "is_active BOOLEAN DEFAULT TRUE, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
            stmt.executeUpdate(createUsersTable);
            
            // Create waste_categories table
            String createCategoriesTable = "CREATE TABLE IF NOT EXISTS waste_categories (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "name VARCHAR(100) NOT NULL, " +
                "description TEXT, " +
                "is_active BOOLEAN DEFAULT TRUE, " +
                "created_by INT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
            stmt.executeUpdate(createCategoriesTable);
            
            // Create waste_types table
            String createTypesTable = "CREATE TABLE IF NOT EXISTS waste_types (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "category_id INT NOT NULL, " +
                "name VARCHAR(100) NOT NULL, " +
                "description TEXT, " +
                "price DECIMAL(10,2) NOT NULL, " +
                "unit VARCHAR(20) NOT NULL, " +
                "is_active BOOLEAN DEFAULT TRUE, " +
                "created_by INT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (category_id) REFERENCES waste_categories(id)" +
                ")";
            stmt.executeUpdate(createTypesTable);
            
            // Create otp_records table
            String createOTPTable = "CREATE TABLE IF NOT EXISTS otp_records (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "phone_number VARCHAR(15) NOT NULL, " +
                "otp_code VARCHAR(6) NOT NULL, " +
                "is_used BOOLEAN DEFAULT FALSE, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
            stmt.executeUpdate(createOTPTable);
            
            // Insert default admin user if not exists
            String insertAdmin = "INSERT IGNORE INTO users (username, password, email, phone_number, role) " +
                               "VALUES ('admin', 'admin123', 'admin@ewaste.com', '081234567890', 'ADMIN')";
            stmt.executeUpdate(insertAdmin);
            
            // Close temporary connection
            tempConn.close();
            
            JOptionPane.showMessageDialog(null,
                "Database and tables created successfully!",
                "Database Setup",
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Failed to create database: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Method to check database connectivity
    public static boolean testConnection() {
        try {
            getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Method to close connection
    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}