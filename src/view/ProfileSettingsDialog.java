package view;

import controller.UserController;
import model.User;
import javax.swing.*;
import java.awt.*;

public class ProfileSettingsDialog extends JDialog {
    private UserController userController;
    private User currentUser;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressArea;
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    
    public ProfileSettingsDialog(JFrame parent, User user) {
        super(parent, "Profile Settings", true);
        this.userController = new UserController();
        this.currentUser = user;
        initComponents();
    }
    
    private void initComponents() {
        setSize(400, 500);
        setLocationRelativeTo(getOwner());
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Profile tab
        JPanel profilePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Email field
        gbc.gridx = 0; gbc.gridy = 0;
        profilePanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        emailField = new JTextField(currentUser.getEmail(), 20);
        profilePanel.add(emailField, gbc);
        
        // Phone field
        gbc.gridx = 0; gbc.gridy = 1;
        profilePanel.add(new JLabel("Phone:"), gbc);
        
        gbc.gridx = 1;
        phoneField = new JTextField(currentUser.getPhoneNumber(), 20);
        profilePanel.add(phoneField, gbc);
        
        // Address field
        gbc.gridx = 0; gbc.gridy = 2;
        profilePanel.add(new JLabel("Address:"), gbc);
        
        gbc.gridx = 1;
        addressArea = new JTextArea(currentUser.getAddress(), 3, 20);
        addressArea.setLineWrap(true);
        profilePanel.add(new JScrollPane(addressArea), gbc);
        
        // Save profile button
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton saveProfileButton = new JButton("Save Profile");
        saveProfileButton.addActionListener(e -> saveProfile());
        profilePanel.add(saveProfileButton, gbc);
        
        // Password tab
        JPanel passwordPanel = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Old password field
        gbc.gridx = 0; gbc.gridy = 0;
        passwordPanel.add(new JLabel("Old Password:"), gbc);
        
        gbc.gridx = 1;
        oldPasswordField = new JPasswordField(20);
        passwordPanel.add(oldPasswordField, gbc);
        
        // New password field
        gbc.gridx = 0; gbc.gridy = 1;
        passwordPanel.add(new JLabel("New Password:"), gbc);
        
        gbc.gridx = 1;
        newPasswordField = new JPasswordField(20);
        passwordPanel.add(newPasswordField, gbc);
        
        // Confirm password field
        gbc.gridx = 0; gbc.gridy = 2;
        passwordPanel.add(new JLabel("Confirm Password:"), gbc);
        
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        passwordPanel.add(confirmPasswordField, gbc);
        
        // Change password button
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.addActionListener(e -> changePassword());
        passwordPanel.add(changePasswordButton, gbc);
        
        // Add tabs
        tabbedPane.addTab("Profile", profilePanel);
        tabbedPane.addTab("Password", passwordPanel);
        
        add(tabbedPane);
    }
    
    private void saveProfile() {
        currentUser.setEmail(emailField.getText().trim());
        currentUser.setPhoneNumber(phoneField.getText().trim());
        currentUser.setAddress(addressArea.getText().trim());
        
        if (userController.updateProfile(currentUser)) {
            JOptionPane.showMessageDialog(this, "Profile updated successfully");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update profile");
        }
    }

    private void changePassword() {
        String oldPassword = new String(oldPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all password fields");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match");
            return;
        }
        
        if (newPassword.length() < 8) {
            JOptionPane.showMessageDialog(this, "New password must be at least 8 characters");
            return;
        }
        
        if (userController.changePassword(currentUser.getId(), oldPassword, newPassword)) {
            JOptionPane.showMessageDialog(this, "Password changed successfully");
            clearPasswordFields();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to change password");
        }
    }
    
    private void clearPasswordFields() {
        oldPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }
}