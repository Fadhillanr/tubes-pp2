package view;

import controller.UserController;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    private UserController userController;
    JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel forgotPasswordLabel;
    private static MainFrame mainFrame;
    private JLabel errorLabel;

    public LoginFrame() {
        userController = new UserController();
        initComponents();
        setupLayout();
        setupListeners();
    }

    private void initComponents() {
        setTitle("E-Waste Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        
        // Updated forgot password label with underline
        forgotPasswordLabel = new JLabel("<html><u>Forgot Password?</u></html>");
        forgotPasswordLabel.setForeground(new Color(32, 80, 114)); // #205072
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        mainPanel.setBackground(new Color(207, 244, 210)); // #CFF4D2

        JLabel titleLabel = new JLabel("E-Waste Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(50, 157, 156)); // #329D9C
        titleLabel. setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Updated labels with new font and color
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setForeground(new Color(86, 197, 150)); // #56c596
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setForeground(new Color(86, 197, 150)); // #56c596
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        
        // Adding rounded box behind username field
        JPanel usernamePanel = new JPanel();
        usernamePanel.setBackground(new Color(50, 157, 156)); // #329D9C
        usernamePanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 5, true));
        usernamePanel.setLayout(new BorderLayout());
        usernamePanel.add(usernameField, BorderLayout.CENTER);
        formPanel.add(usernamePanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;

        // Adding rounded box behind password field
        JPanel passwordPanel = new JPanel();
        passwordPanel.setBackground(new Color(50, 157, 156)); // #329D9C
        passwordPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 5, true));
        passwordPanel.setLayout(new BorderLayout());
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        formPanel.add(passwordPanel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        // Set font for buttons
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(123, 228, 149)); // Lighter shade for visibility
        loginButton.setForeground(new Color(32, 80, 114)); // #205072
        
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setBackground(new Color(123, 228, 149)); // Lighter shade for visibility
        registerButton.setForeground(new Color(32, 80, 114)); // #205072
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        JPanel errorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        errorPanel.add(errorLabel);

        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(40));
        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(errorPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        JPanel forgotPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        // Set font for forgot password label
        forgotPasswordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        forgotPanel.add(forgotPasswordLabel);
        mainPanel.add(forgotPanel);

        add(mainPanel);

        mainPanel.setBackground(new Color(207, 244, 210)); // #CFF4D2
        formPanel.setBackground(new Color(207, 244, 210)); // #CFF4D2
        buttonPanel.setBackground(new Color(207, 244, 210)); // #CFF4D2
        forgotPanel.setBackground(new Color(207, 244, 210)); // #CFF4D2
        errorPanel.setBackground(new Color(207, 244, 210)); // #CFF4D2

        loginButton.setPreferredSize(new Dimension(100, 30));
        registerButton.setPreferredSize(new Dimension(100, 30));
    }

    private void setupListeners() {
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> openRegistrationFrame());

        forgotPasswordLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openResetPasswordDialog();
            }
        });

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        });
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields");
            return;
        }

        User user = userController.login(username, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this,
                    "Welcome, " + user.getUsername() + "!",
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            errorLabel.setText("");
            openMainFrame(user);
        } else {
            errorLabel.setText("Invalid username or password");
            passwordField.setText("");
        }
    }

    private void openRegistrationFrame() {
        setVisible(false);
        new RegistrationFrame().setVisible(true);
    }

    private void openResetPasswordDialog() {
        ResetPasswordDialog resetDialog = new ResetPasswordDialog(this);
        resetDialog.setVisible(true);
    }

    private void openMainFrame(User user) {
        if (mainFrame != null) {
            mainFrame.dispose();
        }
        mainFrame = new MainFrame(user);
        mainFrame.setVisible(true);
        setVisible(false);
    }

    public void centerDialog(JDialog dialog) {
        final int x = this.getX() + (this.getWidth() - dialog.getWidth()) / 2;
        final int y = this.getY() + (this.getHeight() - dialog.getHeight()) / 2;
        dialog.setLocation(x, y);
    }

    public static MainFrame getMainFrame() {
        return mainFrame;
    }
}