package view;

import controller.UserController;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    private UserController userController;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel forgotPasswordLabel;
    private static MainFrame mainFrame;
    
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
        forgotPasswordLabel = new JLabel("Forgot Password?");
        forgotPasswordLabel.setForeground(Color.BLUE);
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        JLabel titleLabel = new JLabel("E-Waste Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(40));
        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        JPanel forgotPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        forgotPanel.add(forgotPasswordLabel);
        mainPanel.add(forgotPanel);
        
        add(mainPanel);
        
        mainPanel.setBackground(Color.WHITE);
        formPanel.setBackground(Color.WHITE);
        buttonPanel.setBackground(Color.WHITE);
        forgotPanel.setBackground(Color.WHITE);
        
        loginButton.setPreferredSize(new Dimension(100, 30));
        registerButton.setPreferredSize(new Dimension(100, 30));
        
        try {
            loginButton.setIcon(new ImageIcon(getClass().getResource("/icons/login.png")));
            registerButton.setIcon(new ImageIcon(getClass().getResource("/icons/register.png")));
        } catch (Exception e) {
            // Icons not available
        }
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
            JOptionPane.showMessageDialog(this,
                "Please fill in all fields",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        User user = userController.login(username, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this,
                "Welcome, " + user.getUsername() + "!",
                "Login Successful",
                JOptionPane.INFORMATION_MESSAGE);
                
            openMainFrame(user);
        } else {
            JOptionPane.showMessageDialog(this,
                "Invalid username or password",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
    
private void openRegistrationFrame() {
       dispose(); 
       new RegistrationFrame().setVisible(true);
   }
    
    private void openResetPasswordDialog() {
        ResetPasswordDialog resetDialog = new ResetPasswordDialog(this);
        resetDialog.setVisible(true);
    }
    
    private void openMainFrame(User user) {
        if (mainFrame == null) {
            mainFrame = new MainFrame(user);
        }
        mainFrame.setVisible(true);
        this.dispose();
    }
    
    public void centerDialog(JDialog dialog) {
        final int x = this.getX() + (this.getWidth() - dialog.getWidth()) / 2;
        final int y = this.getY() + (this.getHeight() - dialog.getHeight()) / 2;
        dialog.setLocation(x, y);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}