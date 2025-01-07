package controller;

import java.sql.*;
import java.util.Random;
import javax.swing.JOptionPane;

public class OTPController {
    private Connection conn;
    private static final int OTP_VALIDITY_MINUTES = 5;
    
    public OTPController() {
        conn = DatabaseConnection.getConnection();
    }
    
    public String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // 6-digit OTP
        return String.valueOf(otp);
    }
    
    public boolean saveOTP(String phoneNumber, String otp) {
        try {
            String sql = "INSERT INTO otp_records (phone_number, otp_code, created_at, is_used) " +
                        "VALUES (?, ?, NOW(), false)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, phoneNumber);
            pstmt.setString(2, otp);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean verifyOTP(String phoneNumber, String otp) {
        try {
            String sql = "SELECT * FROM otp_records WHERE phone_number = ? AND otp_code = ? " +
                        "AND is_used = false AND created_at >= NOW() - INTERVAL ? MINUTE " +
                        "ORDER BY created_at DESC LIMIT 1";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, phoneNumber);
            pstmt.setString(2, otp);
            pstmt.setInt(3, OTP_VALIDITY_MINUTES);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Mark OTP as used
                String updateSql = "UPDATE otp_records SET is_used = true WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, rs.getInt("id"));
                updateStmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}