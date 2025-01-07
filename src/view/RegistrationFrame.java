package view;

import controller.UserController;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class RegistrationFrame extends JFrame {
    private UserController userController;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressArea;
    
    private LoginFrame loginForm;
    public RegistrationFrame() {
       userController = new UserController();
       initComponents();
       setupFocusHandling();
       setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
   }
    
    private void setupFocusHandling() {
        Component[] components = {
            usernameField, passwordField, confirmPasswordField,
            emailField, phoneField, addressArea
        };
        
        KeyListener tabListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    e.consume();
                    Component comp = (Component)e.getSource();
                    if (e.isShiftDown()) {
                        comp.transferFocusBackward();
                    } else {
                        comp.transferFocus();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        };

        for (Component comp : components) {
            comp.setFocusTraversalKeysEnabled(false);
            comp.addKeyListener(tabListener);
        }

        usernameField.requestFocusInWindow();
        setFocusCycleRoot(true);
    }
    
    private void initComponents() {
        setTitle("E-Waste Management System - Registration");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JLabel titleLabel = new JLabel("User Registration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        mainPanel.add(usernameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        mainPanel.add(passwordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        mainPanel.add(confirmPasswordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        mainPanel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        mainPanel.add(phoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        mainPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        mainPanel.add(new JScrollPane(addressArea), gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton registerButton = new JButton("Register");
        JButton cancelButton = new JButton("Cancel");
        JButton loginButton = new JButton("Login");
        
        registerButton.addActionListener(e -> handleRegistration());
        cancelButton.addActionListener(e -> handleCancel());
        loginButton.addActionListener(e -> handleLogin());
        
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(loginButton);
        
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel);
    }
    
     private void handleRegistration() {
       if (!validateInput()) {
           return;
       }
       
       User user = new User();
       user.setUsername(usernameField.getText().trim());
       user.setPassword(new String(passwordField.getPassword()));
       user.setEmail(emailField.getText().trim());
       user.setPhoneNumber(phoneField.getText().trim());
       user.setAddress(addressArea.getText().trim());
       
       if (userController.register(user)) {
           JOptionPane.showMessageDialog(this,
               "Registration successful! Please login.",
               "Success",
               JOptionPane.INFORMATION_MESSAGE);
           openLoginFrame();
       } else {
           JOptionPane.showMessageDialog(this,
               "Registration failed. Please try again.",
               "Error",
               JOptionPane.ERROR_MESSAGE);
       }
   }
    
    private boolean validateInput() {
        if (usernameField.getText().trim().isEmpty() ||
            passwordField.getPassword().length == 0 ||
            confirmPasswordField.getPassword().length == 0 ||
            emailField.getText().trim().isEmpty() ||
            phoneField.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this,
                "Please fill in all required fields",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!new String(passwordField.getPassword())
                .equals(new String(confirmPasswordField.getPassword()))) {
            JOptionPane.showMessageDialog(this,
                "Passwords do not match",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void handleCancel() {
       dispose();
       new LoginFrame().setVisible(true);
   }

    
    private void openLoginFrame() {
       dispose();
       new LoginFrame().setVisible(true);
   }
    
    private void handleLogin() {
   dispose(); // Tutup form registrasi
   new LoginFrame().setVisible(true); // Buka form login
}
}