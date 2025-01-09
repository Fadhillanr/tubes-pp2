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
        if (category.getName() == null || category.getName().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nama harus diisi!");
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
            String sql = "INSERT INTO waste_categories (name, created_at) "
                    + "VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, category.getName());
            pstmt.setTimestamp(2, new Timestamp(category.getCreatedAt().getTime()));
            int result = pstmt.executeUpdate();
            if (result > 0) {
                conn.commit();
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Failed to add category");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to add category" + e.getMessage());
            return false;
        }

    }

    // Update category
    public boolean update(WasteCategory category) {
        if (!validateInput(category)) {
            return false;
        }

        try {
            String sql = "UPDATE waste_categories SET name = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, category.getName());
            pstmt.setInt(2, category.getId());

            int result = pstmt.executeUpdate();
            if (result > 0) {
                conn.commit();
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Failed to Update Category");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to update Category" + e.getMessage());
            return false;
        }

    }

    // Delete category (hard delete)
    public boolean delete(int id) {
        try {
            String sql = "DELETE FROM waste_categories WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            int result = pstmt.executeUpdate();
            if (result > 0) {
                conn.commit();
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete Waste Category");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to delete Waste Category" + e.getMessage());
            return false;
        }
    }

    // Get all categories
    public List<WasteCategory> getAllCategories() {
        List<WasteCategory> categories = new ArrayList<>();
        try {
            String sql = "SELECT * FROM waste_categories";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                WasteCategory category = new WasteCategory();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
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
            String sql = "SELECT * FROM waste_categories WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                WasteCategory category = new WasteCategory();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                category.setCreatedAt(rs.getTimestamp("created_at"));
                return category;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
