package model;

public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String address;
    private String role;
    private boolean isActive;
    
    // Constructors
    public User() {}
    
    public User(String username, String password, String email, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isActive = true;
        this.role = "USER";
    }
    
    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAddress() { return address; }
    public String getRole() { return role; }
    public boolean isActive() { return isActive; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setAddress(String address) { this.address = address; }
    public void setRole(String role) { this.role = role; }
    public void setActive(boolean active) { isActive = active; }
}
