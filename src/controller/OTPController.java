package controller;

import model.OTPRecord;
import java.sql.*;
import java.util.Random;

public class OTPController {

    private final Connection conn;

    public OTPController() {
        conn = DatabaseConnection.getConnection();
    }

    // Generate OTP
    public String generateOTP() {
        // Generate OTP random 6 digit
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    // Create new otp record
    public boolean create(OTPRecord otpRecord) {
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO otp_records (email, otp_code) "
                    + "VALUES (?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, otpRecord.getEmail());
            pstmt.setString(2, otpRecord.getOtpCode());
            int result = pstmt.executeUpdate();
            conn.commit();
            if (result > 0) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Get otp by email
    public OTPRecord getOTPByEmail(String email) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM otp_records WHERE email = ? AND is_used = false ORDER BY created_at DESC LIMIT 1";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                OTPRecord otpRecord = new OTPRecord();
                otpRecord.setId(rs.getInt("id"));
                otpRecord.setEmail(rs.getString("email"));
                otpRecord.setOtpCode(rs.getString("otp_code"));
                otpRecord.setUsed(rs.getBoolean("is_used"));
                otpRecord.setCreatedAt(rs.getTimestamp("created_at"));
                return otpRecord;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Update otp to used
    public boolean updateToUsed(int id) {
        PreparedStatement pstmt = null;
        try {
            String sql = "UPDATE otp_records SET is_used = true WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            int result = pstmt.executeUpdate();
            conn.commit();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
