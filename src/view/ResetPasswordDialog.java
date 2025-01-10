package view;

import controller.UserController;
import javax.swing.*;
import java.awt.*;

public class ResetPasswordDialog extends JDialog {

    private UserController userController;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    // Warna yang konsisten
    private static final Color PRIMARY_COLOR = new Color(50, 157, 156); // #329D9C
    private static final Color BACKGROUND_COLOR = new Color(207, 244, 210); // #CFF4D2
    private static final Color BUTTON_TEXT_COLOR = new Color(50, 50, 50); // Dark gray for button text
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 14); // Font untuk label


    public ResetPasswordDialog(JFrame parent) {
        super(parent, "Reset Password", true);
        this.userController = new UserController();
        initComponents();
    }


    private void initComponents() {
        setSize(400, 250);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(BACKGROUND_COLOR); //set background

        JPanel mainPanel = new JPanel(new GridBagLayout());
         mainPanel.setBackground(BACKGROUND_COLOR); //set background for panel

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // padding untuk semua komponen
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;


        // New password field
        gbc.gridx = 0;
        gbc.gridy = 0;
         JLabel newPasswordLabel = new JLabel("New Password:");
        newPasswordLabel.setFont(LABEL_FONT);
         newPasswordLabel.setForeground(PRIMARY_COLOR);
        mainPanel.add(newPasswordLabel, gbc);


        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
       mainPanel.add(passwordField, gbc);


        // Confirm password field
        gbc.gridx = 0;
        gbc.gridy = 1;
         JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(LABEL_FONT);
          confirmPasswordLabel.setForeground(PRIMARY_COLOR);
       mainPanel.add(confirmPasswordLabel, gbc);

        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        mainPanel.add(confirmPasswordField, gbc);

       // Reset button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
       JButton resetButton = createButton("Reset Password");
        resetButton.addActionListener(e -> resetPassword());
       JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
      buttonPanel.setBackground(BACKGROUND_COLOR); 
        buttonPanel.add(resetButton);
       mainPanel.add(buttonPanel, gbc);


        add(mainPanel);
    }


    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setBackground(PRIMARY_COLOR);
        button.setOpaque(true);
        return button;
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