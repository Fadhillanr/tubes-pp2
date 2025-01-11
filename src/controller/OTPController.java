package controller;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Random;
import model.OTPRecord;

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

        System.out.println("Email sebelum disimpan ke database: " + otpRecord.getEmail());
        System.out.println("OTP Code: " + otpRecord.getOtpCode());
        // Validasi panjang email sebelum penyimpanan
        if (otpRecord.getEmail().length() > 255) {
            System.err.println("Email is too long. Please use a shorter email");
            return false;
        }
         // Set expired_at to 5 minutes from now
         LocalDateTime now = LocalDateTime.now();
         LocalDateTime expiredAt = now.plusMinutes(5);
         Timestamp expiredAtTimestamp = Timestamp.valueOf(expiredAt);

        try {
            String sql = "INSERT INTO otp_records (email, otp_code, expired_at) "
                    + "VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, otpRecord.getEmail());
            pstmt.setString(2, otpRecord.getOtpCode());
             pstmt.setTimestamp(3, expiredAtTimestamp);
            int result = pstmt.executeUpdate();
            if (result > 0) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();

            return false;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("SQL Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Get otp by email
    public OTPRecord getOTPByEmail(String email) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM otp_records WHERE email = ? AND is_used = false AND expired_at > NOW() ORDER BY created_at DESC LIMIT 1";
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
                 otpRecord.setExpiredAt(rs.getTimestamp("expired_at"));
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
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                  conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    //Save OTP and return boolean
    public boolean saveOTP(String email, String otp){
      OTPRecord otpRecord = new OTPRecord(email, otp);
      return create(otpRecord);
    }

    public boolean verifyOTP(String email, String otp){
      OTPRecord otpRecord = getOTPByEmail(email);
        if (otpRecord != null && otpRecord.getOtpCode().equals(otp)) {
          updateToUsed(otpRecord.getId());
          return true;
        }
        return false;
    }
}