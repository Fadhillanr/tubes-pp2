package view;

import controller.OTPController;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import service.MailService;

public class OTPDialog extends JDialog {
    private OTPController otpController;
    private String email;
    private JTextField otpField;
    private boolean isVerified = false;
    private MailService mailService;

     private static final Color PRIMARY_COLOR = new Color(50, 157, 156); // #329D9C
     private static final Color BUTTON_COLOR = new Color(123, 228, 149); // Lighter shade for visibility
    private static final Color BUTTON_HOVER_COLOR = new Color(220, 220, 220);
     private static final Color BACKGROUND_COLOR = new Color(207, 244, 210); // #CFF4D2

    public OTPDialog(JFrame parent, String email) {
        super(parent, "OTP Verification", true);
        this.otpController = new OTPController();
        this.mailService = new MailService();
        this.email = email;
        initComponents();
        sendOTP();
    }

    private void initComponents() {
        setSize(300, 180);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // OTP field
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Enter OTP:"), gbc);

        gbc.gridx = 1;
        otpField = new JTextField(10);
        mainPanel.add(otpField, gbc);

        // Verify button
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JButton verifyButton = createStyledButton("Verify");
        verifyButton.addActionListener(e -> verifyOTP());
        mainPanel.add(verifyButton, gbc);

        // Resend button
        gbc.gridy = 2;
        JButton resendButton = createStyledButton("Resend OTP");
        resendButton.addActionListener(e -> sendOTP());
        mainPanel.add(resendButton, gbc);

        add(mainPanel);
    }

    private void sendOTP() {
        String otp = otpController.generateOTP();
        if (otpController.saveOTP(email, otp)) {
             // Send OTP via email using MailService
            boolean emailSent = mailService.sendOTP(email, otp);
           if(emailSent){
                 JOptionPane.showMessageDialog(this, "OTP sent to " + email);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to send OTP via email");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save OTP to database");
        }
    }

    private void verifyOTP() {
        String otp = otpField.getText().trim();
        if (otp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter OTP");
            return;
        }

        if (otpController.verifyOTP(email, otp)) {
            isVerified = true;
            JOptionPane.showMessageDialog(this, "OTP verified successfully");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid or expired OTP");
        }
    }

    public boolean isVerified() {
        return isVerified;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(new Color(32, 80, 114)); // #205072
        button.setFocusPainted(false);
         button.setBorder(new CompoundBorder(
                new LineBorder(Color.GRAY, 1),
                new EmptyBorder(8, 15, 8, 15)
        ));


        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_HOVER_COLOR);
                 button.setBorder(new CompoundBorder(
                        new LineBorder(Color.GRAY.darker(), 1),
                        new EmptyBorder(8, 15, 8, 15)
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_COLOR);
                  button.setBorder(new CompoundBorder(
                        new LineBorder(Color.GRAY, 1),
                        new EmptyBorder(8, 15, 8, 15)
                ));
            }
        });

        return button;
    }
}