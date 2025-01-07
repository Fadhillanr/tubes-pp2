package controller;

import model.WasteType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class WasteTypeController {
    private Connection conn;
    
    public WasteTypeController() {
        conn = DatabaseConnection.getConnection();
    }
    
    // Validate input
    private boolean validateInput(WasteType type) {
        if (type.getName().isEmpty() || type.getDescription().isEmpty() || 
            type.getPrice() < 0 || type.getUnit().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Semua field harus diisi dengan benar!");
            return false;
        }
        return true;
    }
    
    // Create new waste type
    public boolean create(WasteType type) {
        if (!validateInput(type)) {
            return false;
        }
        
        try {
            String sql = "INSERT INTO waste_types (category_id, name, description, price, unit, " +
                        "is_active, created_by, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, type.getCategoryId());
            pstmt.setString(2, type.getName());
            pstmt.setString(3, type.getDescription());
            pstmt.setDouble(4, type.getPrice());
            pstmt.setString(5, type.getUnit());
            pstmt.setBoolean(6, type.isActive());
            pstmt.setString(7, type.getCreatedBy());
            pstmt.setTimestamp(8, new Timestamp(type.getCreatedAt().getTime()));
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update waste type
    public boolean update(WasteType type) {
        if (!validateInput(type)) {
            return false;
        }
        
        try {
            String sql = "UPDATE waste_types SET category_id = ?, name = ?, description = ?, " +
                        "price = ?, unit = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, type.getCategoryId());
            pstmt.setString(2, type.getName());
            pstmt.setString(3, type.getDescription());
            pstmt.setDouble(4, type.getPrice());
            pstmt.setString(5, type.getUnit());
            pstmt.setInt(6, type.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete waste type (soft delete)
    public boolean delete(int id) {
        try {
            String sql = "UPDATE waste_types SET is_active = false WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get all waste types
    public List<WasteType> getAllWasteTypes() {
        List<WasteType> types = new ArrayList<>();
        try {
            String sql = "SELECT * FROM waste_types WHERE is_active = true";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                WasteType type = new WasteType();
                type.setId(rs.getInt("id"));
                type.setCategoryId(rs.getInt("category_id"));
                type.setName(rs.getString("name"));
                type.setDescription(rs.getString("description"));
                type.setPrice(rs.getDouble("price"));
                type.setUnit(rs.getString("unit"));
                type.setActive(rs.getBoolean("is_active"));
                type.setCreatedBy(rs.getString("created_by"));
                type.setCreatedAt(rs.getTimestamp("created_at"));
                types.add(type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return types;
    }
    
    // Get waste types by category
    public List<WasteType> getWasteTypesByCategory(int categoryId) {
        List<WasteType> types = new ArrayList<>();
        try {
            String sql = "SELECT * FROM waste_types WHERE category_id = ? AND is_active = true";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, categoryId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                WasteType type = new WasteType();
                type.setId(rs.getInt("id"));
                type.setCategoryId(rs.getInt("category_id"));
                type.setName(rs.getString("name"));
                type.setDescription(rs.getString("description"));
                type.setPrice(rs.getDouble("price"));
                type.setUnit(rs.getString("unit"));
                type.setActive(rs.getBoolean("is_active"));
                type.setCreatedBy(rs.getString("created_by"));
                type.setCreatedAt(rs.getTimestamp("created_at"));
                types.add(type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return types;
    }
}