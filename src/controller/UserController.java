package controller;

import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class UserController {
    private Connection conn;
    
    public UserController() {
        conn = DatabaseConnection.getConnection();
    }
    
    // Validasi input
    public boolean validateInput(User user) {
        if (user.getUsername().isEmpty() || user.getPassword().isEmpty() || 
            user.getEmail().isEmpty() || user.getPhoneNumber().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Semua field harus diisi!");
            return false;
        }
        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(null, "Format email tidak valid!");
            return false;
        }
        if (!user.getPhoneNumber().matches("^[0-9]{10,13}$")) {
            JOptionPane.showMessageDialog(null, "Nomor telepon tidak valid!");
            return false;
        }
        if (user.getPassword().length() < 8) {
            JOptionPane.showMessageDialog(null, "Password minimal 8 karakter!");
            return false;
        }
        return true;
    }
    
    // Register user baru
    public boolean register(User user) {
        if (!validateInput(user)) {
            return false;
        }
        
        try {
            String sql = "INSERT INTO users (username, password, email, phone_number, address, role, is_active) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword()); // In real app, password should be hashed
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPhoneNumber());
            pstmt.setString(5, user.getAddress());
            pstmt.setString(6, user.getRole());
            pstmt.setBoolean(7, user.isActive());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Login
    public User login(String username, String password) {
    try {
        // Hapus AND is_active = true dari query
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, username);
        pstmt.setString(2, password);
        
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            user.setPhoneNumber(rs.getString("phone_number"));
            user.setAddress(rs.getString("address"));
            user.setRole(rs.getString("role"));
            user.setActive(rs.getBoolean("is_active"));
            return user;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}
    
    // Update profile
    public boolean updateProfile(User user) {
        try {
            String sql = "UPDATE users SET email = ?, phone_number = ?, address = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getPhoneNumber());
            pstmt.setString(3, user.getAddress());
            pstmt.setInt(4, user.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Change password
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        try {
            // Verify old password
            String checkSql = "SELECT password FROM users WHERE id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, userId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getString("password").equals(oldPassword)) {
                String updateSql = "UPDATE users SET password = ? WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, newPassword);
                updateStmt.setInt(2, userId);
                return updateStmt.executeUpdate() > 0;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Reset password
    public boolean resetPassword(String email, String newPassword) {
        try {
            String sql = "UPDATE users SET password = ? WHERE email = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newPassword);
            pstmt.setString(2, email);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get all users
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            String sql = "SELECT * FROM users WHERE is_active = true";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setAddress(rs.getString("address"));
                user.setRole(rs.getString("role"));
                user.setActive(rs.getBoolean("is_active"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}