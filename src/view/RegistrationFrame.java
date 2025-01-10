package view;

import controller.UserController;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
import model.User;

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
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    e.consume();
                    Component comp = (Component) e.getSource();
                    if (e.isShiftDown()) {
                        comp.transferFocusBackward();
                    } else {
                        comp.transferFocus();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
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
        mainPanel.setBackground(new Color(207, 244, 210)); // #CFF4D2
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("User Registration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(50, 157, 156)); // #329D9C
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 20, 0); // Add padding above and below the title
        mainPanel.add(titleLabel, gbc);

        gbc.insets = new Insets(5, 5, 5, 5); // Reset insets for other components
        gbc.gridwidth = 1;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setForeground(new Color(86, 197, 150)); // #56c596
        mainPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(20);
        mainPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setForeground(new Color(86, 197, 150)); // #56c596
        mainPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        mainPanel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        confirmPasswordLabel.setForeground(new Color(86, 197, 150)); // #56c596
        mainPanel.add(confirmPasswordLabel, gbc);

        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        mainPanel.add(confirmPasswordField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emailLabel.setForeground(new Color(86, 197, 150)); // #56c596
        mainPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        mainPanel.add(emailField, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 14));
        phoneLabel.setForeground(new Color(86, 197, 150)); // #56c596
        mainPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        phoneField = new JTextField(20);
        mainPanel.add(phoneField, gbc);

        // Address
        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font("Arial", Font.BOLD, 14));
        addressLabel.setForeground(new Color(86, 197, 150)); // #56c596
        mainPanel.add(addressLabel, gbc);

        gbc.gridx = 1;
        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        mainPanel.add(new JScrollPane(addressArea), gbc);

        // Button Panel
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

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
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
        String email = emailField.getText().trim();
        if (email.length() > 255) {
            JOptionPane.showMessageDialog(this,
                    "Email is too long. Please use a shorter email",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        user.setEmail(email);
        user.setPhoneNumber(phoneField.getText().trim());
        user.setAddress(addressArea.getText().trim());

        boolean isRegistered = userController.register(user);
        if (isRegistered) {
            JOptionPane.showMessageDialog(this, "Registration successful!");

            // Open OTP dialog and wait for verification
            OTPDialog otpDialog = new OTPDialog(this, user.getEmail());
            otpDialog.setVisible(true);

            if (otpDialog.isVerified()) {
                JOptionPane.showMessageDialog(this, "Registration successful! Please login.");
                openLoginFrame();
            } else {
                JOptionPane.showMessageDialog(this, "OTP verification failed. Registration canceled");
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Registration failed. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateInput() {
        if (usernameField.getText().trim().isEmpty()
                || passwordField.getPassword().length == 0
                || confirmPasswordField.getPassword().length == 0
                || emailField.getText().trim().isEmpty()
                || phoneField.getText().trim().isEmpty()) {

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
        dispose();
        new LoginFrame().setVisible(true);
    }
}
