package view;

import controller.UserController;
import javax.swing.*;
import java.awt.*;

public class ResetPasswordDialog extends JDialog {

    private UserController userController;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public ResetPasswordDialog(JFrame parent) {
        super(parent, "Reset Password", true);
        this.userController = new UserController();
        initComponents();
    }

    private void initComponents() {
        setSize(400, 250); // Perbesar ukuran dialog
        setLocationRelativeTo(getOwner());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // New password field
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("New Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        mainPanel.add(passwordField, gbc);

        // Confirm password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Confirm Password:"), gbc);

        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        mainPanel.add(confirmPasswordField, gbc);

        // Reset button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton resetButton = new JButton("Reset Password");
        resetButton.addActionListener(e -> resetPassword());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(resetButton);
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
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

        LoginFrame loginFrame = (LoginFrame) getOwner();
        if (userController.resetPassword(loginFrame.usernameField.getText().trim(), newPassword)) {
            JOptionPane.showMessageDialog(this, "Password reset successfully, please log in again");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to reset password");
        }
    }
}
