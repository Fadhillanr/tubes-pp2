package view;

import controller.UserController;
import javax.swing.*;
import java.awt.*;

public class ResetPasswordDialog extends JDialog {
    private UserController userController;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private String resetToken;
    
    public ResetPasswordDialog(JFrame parent) {
        super(parent, "Reset Password", true);
        this.userController = new UserController();
        initEmailComponents();
    }
    
    private void initEmailComponents() {
        setSize(300, 150);
        setLocationRelativeTo(getOwner());
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Email field
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        emailField = new JTextField(20);
        mainPanel.add(emailField, gbc);
        
        // Send reset link button
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        JButton sendButton = new JButton("Send Reset Link");
        sendButton.addActionListener(e -> sendResetLink());
        mainPanel.add(sendButton, gbc);
        
        add(mainPanel);
    }
    
    private void initResetComponents() {
        getContentPane().removeAll();
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // New password field
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("New Password:"), gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        mainPanel.add(passwordField, gbc);
        
        // Confirm password field
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Confirm Password:"), gbc);
        
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        mainPanel.add(confirmPasswordField, gbc);
        
        // Reset button
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton resetButton = new JButton("Reset Password");
        resetButton.addActionListener(e -> resetPassword());
        mainPanel.add(resetButton, gbc);
        
        add(mainPanel);
        pack();
        setLocationRelativeTo(getOwner());
    }
    
    private void sendResetLink() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter email");
            return;
        }
        
        // In a real application, generate and send reset token via email
        resetToken = "sample_token"; // This should be a secure random token
        JOptionPane.showMessageDialog(this, 
            "Reset link has been sent to your email\nFor testing, click OK to proceed");
        initResetComponents();
    }
    
    private void resetPassword() {
        String newPassword = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match");
            return;
        }
        
        if (newPassword.length() < 8) {
            JOptionPane.showMessageDialog(this, "Password must be at least 8 characters");
            return;
        }
        
        if (userController.resetPassword(emailField.getText().trim(), newPassword)) {
            JOptionPane.showMessageDialog(this, "Password reset successfully");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to reset password");
        }
    }
}