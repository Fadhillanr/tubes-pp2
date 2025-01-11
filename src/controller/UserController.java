package controller;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import model.User;
import org.mindrot.jbcrypt.BCrypt;
import java.time.LocalDate;

public class UserController {

    private static final String UPLOAD_DIR = "uploads/profile_images";
    private final Connection conn;
    private static final int MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_IMAGE_TYPES = {
        "image/jpeg", "image/png", "image/gif"
    };

    // Constructor with directory creation
    public UserController() {
        conn = DatabaseConnection.getConnection();
        createUploadDirectory();
    }

    private void createUploadDirectory() {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    // Validate and process image file
    private boolean validateImage(File file) {
        if (file.length() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("File size exceeds 5MB limit");
        }

        try {
            String contentType = Files.probeContentType(file.toPath());
            boolean validType = false;
            for (String type : ALLOWED_IMAGE_TYPES) {
                if (type.equals(contentType)) {
                    validType = true;
                    break;
                }
            }
            if (!validType) {
                throw new IllegalArgumentException("Invalid file type. Allowed types: JPG, PNG, GIF");
            }

            // Validate image dimensions
            BufferedImage img = ImageIO.read(file);
            if (img == null) {
                throw new IllegalArgumentException("Invalid image file");
            }
            return true;
        } catch (IOException e) {
            throw new IllegalArgumentException("Error processing image file: " + e.getMessage());
        }
    }

    // Save profile image with proper error handling
    public String saveProfileImage(File sourceFile, int userId) throws IOException {
        if (sourceFile == null || !sourceFile.exists()) {
            throw new IllegalArgumentException("Invalid source file");
        }

        try {
            // Validate image before processing
            if (!validateImage(sourceFile)) {
                return null;
            }

            // Generate unique filename
            String fileExtension = sourceFile.getName().substring(sourceFile.getName().lastIndexOf("."));
            String fileName = "profile_" + userId + "_" + System.currentTimeMillis() + fileExtension;
            Path targetPath = Paths.get(UPLOAD_DIR, fileName);

            // Optimize and save image
            BufferedImage originalImage = ImageIO.read(sourceFile);
            BufferedImage resizedImage = resizeImage(originalImage, 800, 800); // Max dimensions

            File optimizedFile = new File(targetPath.toString());
            ImageIO.write(resizedImage, fileExtension.substring(1), optimizedFile);

            return targetPath.toString();
        } catch (IOException e) {
            throw new IOException("Failed to save profile image: " + e.getMessage());
        }
    }

    // Resize image while maintaining aspect ratio
    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        double ratio = Math.min(
                (double) targetWidth / originalImage.getWidth(),
                (double) targetHeight / originalImage.getHeight()
        );

        int width = (int) (originalImage.getWidth() * ratio);
        int height = (int) (originalImage.getHeight() * ratio);

        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = resizedImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.drawImage(originalImage, 0, 0, width, height, null);
        graphics.dispose();

        return resizedImage;
    }
     private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private boolean checkPassword(String password, String hashedPassword) {
         if (hashedPassword == null || hashedPassword.isEmpty() ) {
             return false;
         }
        return BCrypt.checkpw(password, hashedPassword);
    }
     // Updated updateProfile method with better error handling
    public boolean updateProfile(User user, File newProfileImage) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // Cek duplikasi username
            if (!user.getUsername().equals(getCurrentUsername(user.getId()))) {
                String checkUser = "SELECT username FROM users "
                        + "WHERE username = ? ";
                pstmt = conn.prepareStatement(checkUser);
                pstmt.setString(1, user.getUsername());
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(null, "Username already exists!", "Update Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            String imagePath = null;
            if (newProfileImage != null) {
                try {
                    imagePath = saveProfileImage(newProfileImage, user.getId());

                    // Delete old profile image if it exists
                    if (user.getProfileImagePath() != null) {
                        File oldImage = new File(user.getProfileImagePath());
                        if (oldImage.exists() && oldImage.isFile()) {
                            oldImage.delete();
                        }
                    }
                } catch (IOException e) {
                    throw new SQLException("Failed to process profile image: " + e.getMessage());
                }
            }

            String sql = "UPDATE users SET username = ?, email = ?, phone_number = ?, address = ?, profile_image_path = ?, birthdate = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPhoneNumber());
            pstmt.setString(4, user.getAddress());
            pstmt.setString(5, imagePath != null ? imagePath : user.getProfileImagePath());
            pstmt.setDate(6, user.getBirthdate() != null ? Date.valueOf(user.getBirthdate()) : null);
            pstmt.setInt(7, user.getId());

            int result = pstmt.executeUpdate();
           
            if (result > 0) {
                user.setProfileImagePath(imagePath != null ? imagePath : user.getProfileImagePath());
                return true;
            }
            return false;

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                e.printStackTrace();
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to update profile: " + e.getMessage());
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                   conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

     private String getCurrentUsername(int userId) {
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     String username = null;
     try {
         String sql = "SELECT username FROM users WHERE id = ?";
         pstmt = conn.prepareStatement(sql);
         pstmt.setInt(1, userId);
         rs = pstmt.executeQuery();
         if (rs.next()) {
            username = rs.getString("username");
         }
         return username;
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

     public boolean resetPassword(String username, String newPassword) {
        PreparedStatement pstmt = null;
        try {
            // Pertama cek apakah username ada
            String checkSql = "SELECT id FROM users WHERE username = ?";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                return false; // Username tidak ditemukan
            }

            // Update password
            String updateSql = "UPDATE users SET password = ? WHERE username = ?";
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setString(1, hashPassword(newPassword)); // Idealnya password harus di-hash
            pstmt.setString(2, username);
            int result = pstmt.executeUpdate();
          
            if (result > 0) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
              try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                e.printStackTrace();
            }
             e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Database error during reset password: " + e.getMessage(),
                    "Reset Error",
                    JOptionPane.ERROR_MESSAGE);
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

    public User login(String username, String password) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM users WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setAddress(rs.getString("address"));
                user.setProfileImagePath(rs.getString("profile_image_path"));
                user.setRole(rs.getString("role"));
                Date birthdate = rs.getDate("birthdate");
                if (birthdate != null) {
                   user.setBirthdate(birthdate.toLocalDate());
                }
                 // Password yang di database adalah hasil dari hash
                 if (checkPassword(password, rs.getString("password"))) {
                    return user; // Cocok
                }
            }
            return null; // Return null jika login gagal

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

    public boolean register(User user) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Validasi data user
            if (!user.isValidUsername() || !user.isValidPassword()
                    || !user.isValidEmail() || !user.isValidPhoneNumber()) {
                JOptionPane.showMessageDialog(null,
                        "Please check your input data format",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Cek duplikasi data
            String checkUser = "SELECT username, email, phone_number FROM users "
                    + "WHERE username = ? OR email = ? OR phone_number = ?";
            pstmt = conn.prepareStatement(checkUser);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPhoneNumber());
            rs = pstmt.executeQuery();

            if (rs.next()) {
                String message;
                if (rs.getString("username").equals(user.getUsername())) {
                    message = "Username already exists!";
                } else if (rs.getString("email").equals(user.getEmail())) {
                    message = "Email already registered!";
                } else {
                    message = "Phone number already registered!";
                }
                JOptionPane.showMessageDialog(null, message, "Registration Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Insert new user
            String sql = "INSERT INTO users (username, password, email, phone_number, address, role, is_active, birthdate) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, user.getUsername());
             pstmt.setString(2, hashPassword(user.getPassword()));
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPhoneNumber());
            pstmt.setString(5, user.getAddress());
            pstmt.setString(6, user.getRole());
            pstmt.setBoolean(7, user.isActive());
             pstmt.setDate(8, user.getBirthdate() != null ? Date.valueOf(user.getBirthdate()) : null);


            int result = pstmt.executeUpdate();
             conn.commit();
            if (result > 0) {
                JOptionPane.showMessageDialog(null,
                        "Registration successful!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Database error during registration: " + e.getMessage(),
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                e.printStackTrace();
            }
            return false;
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

    public boolean deleteAccount(int userId) {
        PreparedStatement pstmt = null;
        try {
            String sql = "DELETE FROM users WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
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
                 conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}