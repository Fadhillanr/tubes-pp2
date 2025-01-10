package model;

import java.io.File;
import java.io.Serializable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDate;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    // Core attributes
    private int id;
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String address;
    private String role;
    private boolean isActive;

    // Profile image attributes
    private String profileImagePath;
    private transient BufferedImage profileImage; // transient to prevent serialization
    private static final int MAX_IMAGE_WIDTH = 800;
    private static final int MAX_IMAGE_HEIGHT = 800;
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB

    //Birthdate
    private LocalDate birthdate;

    // Constructors
    public User() {
        this.isActive = true;
        this.role = "USER";
    }

    public User(String username, String password, String email, String phoneNumber) {
        this();
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
    
     public User(String username, String password, String email, String phoneNumber, LocalDate birthdate) {
        this();
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthdate = birthdate;
    }


    public User(String username, String password, String role) {
        this();
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Profile Image Methods
    public boolean loadProfileImage() {
        if (profileImagePath != null && !profileImagePath.isEmpty()) {
            try {
                File imageFile = new File(profileImagePath);
                if (imageFile.exists()) {
                    profileImage = ImageIO.read(imageFile);
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean validateProfileImage(File imageFile) {
        if (imageFile == null || !imageFile.exists()) {
            return false;
        }

        // Check file size
        if (imageFile.length() > MAX_IMAGE_SIZE) {
            return false;
        }

        try {
            // Validate image format and dimensions
            BufferedImage img = ImageIO.read(imageFile);
            if (img == null) {
                return false;
            }

            // Check image type
            String fileName = imageFile.getName().toLowerCase();
            if (!fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg")
                    && !fileName.endsWith(".png") && !fileName.endsWith(".gif")) {
                return false;
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getRole() {
        return role;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public BufferedImage getProfileImage() {
        if (profileImage == null) {
            loadProfileImage();
        }
        return profileImage;
    }
    
     public LocalDate getBirthdate() {
        return birthdate;
    }
    

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username != null ? username.trim() : null;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim() : null;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber != null ? phoneNumber.trim() : null;
    }

    public void setAddress(String address) {
        this.address = address != null ? address.trim() : null;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
        // Reset cached image when path changes
        this.profileImage = null;
    }
    
     public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    // Validation Methods
    public boolean isValidUsername() {
        return username != null && username.length() >= 3 && username.length() <= 50;
    }

    public boolean isValidPassword() {
        return password != null && password.length() >= 8;
    }

    public boolean isValidEmail() {
        if (email == null || email.isEmpty()) {
            return false;
        }
        // Basic email validation
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public boolean isValidPhoneNumber() {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }
        // Basic phone number validation
        return phoneNumber.matches("^[0-9+()-]{10,15}$");
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "User{"
                + "id=" + id
                + ", username='" + username + '\''
                + ", email='" + email + '\''
                + ", phoneNumber='" + phoneNumber + '\''
                 + ", birthdate='" + birthdate + '\''
                + ", role='" + role + '\''
                + ", isActive=" + isActive
                + ", hasProfileImage=" + (profileImagePath != null)
                + '}';
    }

    // equals and hashCode methods
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return 31 * id;
    }
}