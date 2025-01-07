// File: src/controller/WasteCategoryController.java
package controller;

import model.WasteCategory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class WasteCategoryController {
    private Connection conn;
    
    public WasteCategoryController() {
        conn = DatabaseConnection.getConnection();
    }
    
    // Validate input
    private boolean validateInput(WasteCategory category) {
        if (category.getName().isEmpty() || category.getDescription().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nama dan deskripsi harus diisi!");
            return false;
        }
        return true;
    }
    
    // Create new category
    public boolean create(WasteCategory category) {
        if (!validateInput(category)) {
            return false;
        }
        
        try {
            String sql = "INSERT INTO waste_categories (name, description, is_active, created_by, created_at) " +
                        "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getDescription());
            pstmt.setBoolean(3, category.isActive());
            pstmt.setString(4, category.getCreatedBy());
            pstmt.setTimestamp(5, new Timestamp(category.getCreatedAt().getTime()));
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update category
    public boolean update(WasteCategory category) {
        if (!validateInput(category)) {
            return false;
        }
        
        try {
            String sql = "UPDATE waste_categories SET name = ?, description = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getDescription());
            pstmt.setInt(3, category.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete category (soft delete)
    public boolean delete(int id) {
        try {
            String sql = "UPDATE waste_categories SET is_active = false WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get all categories
    public List<WasteCategory> getAllCategories() {
        List<WasteCategory> categories = new ArrayList<>();
        try {
            String sql = "SELECT * FROM waste_categories WHERE is_active = true";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                WasteCategory category = new WasteCategory();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                category.setDescription(rs.getString("description"));
                category.setActive(rs.getBoolean("is_active"));
                category.setCreatedBy(rs.getString("created_by"));
                category.setCreatedAt(rs.getTimestamp("created_at"));
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

            // Get category by ID
    public WasteCategory getCategoryById(int id) {
        try {
            String sql = "SELECT * FROM waste_categories WHERE id = ? AND is_active = true";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                WasteCategory category = new WasteCategory();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                category.setDescription(rs.getString("description"));
                category.setActive(rs.getBoolean("is_active"));
                category.setCreatedBy(rs.getString("created_by"));
                category.setCreatedAt(rs.getTimestamp("created_at"));
                return category;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

