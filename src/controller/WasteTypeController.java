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
        if (type.getName() == null || type.getName().isEmpty() || type.getDescription() == null || type.getDescription().isEmpty()
                || type.getUnit() == null || type.getUnit().isEmpty() || type.getWeight() == null || type.getWeight().isEmpty()) {
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
            String sql = "INSERT INTO waste_types (category_id, name, description, weight, unit, "
                    + "created_at) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, type.getCategoryId());
            pstmt.setString(2, type.getName());
            pstmt.setString(3, type.getDescription());
            pstmt.setString(4, type.getWeight());
            pstmt.setString(5, type.getUnit());
            pstmt.setTimestamp(6, new Timestamp(type.getCreatedAt().getTime()));
            int result = pstmt.executeUpdate();
            if (result > 0) {
                conn.commit();
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Failed to add Waste Type");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to add Waste Type: " + e.getMessage());
            return false;
        }

    }

    // Update waste type
    public boolean update(WasteType type) {
        if (!validateInput(type)) {
            return false;
        }

        try {
            String sql = "UPDATE waste_types SET category_id = ?, name = ?, description = ?, "
                    + "weight = ?, unit = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, type.getCategoryId());
            pstmt.setString(2, type.getName());
            pstmt.setString(3, type.getDescription());
            pstmt.setString(4, type.getWeight());
            pstmt.setString(5, type.getUnit());
            pstmt.setInt(6, type.getId());

            int result = pstmt.executeUpdate();
            if (result > 0) {
                conn.commit();
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Failed to Update Waste Type");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to update Waste Type" + e.getMessage());
            return false;
        }
    }

    // Delete waste type (hard delete)
    public boolean delete(int id) {
        try {
            String sql = "DELETE FROM waste_types WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            System.out.println("SQL: " + pstmt.toString());
            int result = pstmt.executeUpdate();
            if (result > 0) {
                conn.commit();
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete Waste Type");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to delete Waste Type" + e.getMessage());
            return false;
        }
    }

    // Get all waste types
    public List<WasteType> getAllWasteTypes() {
        List<WasteType> types = new ArrayList<>();
        try {
            String sql = "SELECT * FROM waste_types";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                WasteType type = new WasteType();
                type.setId(rs.getInt("id"));
                type.setCategoryId(rs.getInt("category_id"));
                type.setName(rs.getString("name"));
                type.setDescription(rs.getString("description"));
                type.setWeight(rs.getString("weight"));
                type.setUnit(rs.getString("unit"));
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
            String sql = "SELECT * FROM waste_types WHERE category_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, categoryId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                WasteType type = new WasteType();
                type.setId(rs.getInt("id"));
                type.setCategoryId(rs.getInt("category_id"));
                type.setName(rs.getString("name"));
                type.setDescription(rs.getString("description"));
                type.setWeight(rs.getString("weight"));
                type.setUnit(rs.getString("unit"));
                type.setCreatedAt(rs.getTimestamp("created_at"));
                types.add(type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return types;
    }
}
