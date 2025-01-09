package view;

import controller.OTPController;
import javax.swing.*;
import java.awt.*;

public class OTPDialog extends JDialog {
    private OTPController otpController;
    private String phoneNumber;
    private JTextField otpField;
    private boolean isVerified = false;
    
    public OTPDialog(JFrame parent, String phoneNumber) {
        super(parent, "OTP Verification", true);
        this.otpController = new OTPController();
        this.phoneNumber = phoneNumber;
        initComponents();
        sendOTP();
    }
    
    private void initComponents() {
        setSize(300, 150);
        setLocationRelativeTo(getOwner());
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // OTP field
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Enter OTP:"), gbc);
        
        gbc.gridx = 1;
        otpField = new JTextField(10);
        mainPanel.add(otpField, gbc);
        
        // Verify button
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        JButton verifyButton = new JButton("Verify");
        verifyButton.addActionListener(e -> verifyOTP());
        mainPanel.add(verifyButton, gbc);
        
        // Resend button
        gbc.gridy = 2;
        JButton resendButton = new JButton("Resend OTP");
        resendButton.addActionListener(e -> sendOTP());
        mainPanel.add(resendButton, gbc);
        
        add(mainPanel);
    }
    
    private void sendOTP() {
        String otp = otpController.generateOTP();
        if (otpController.saveOTP(phoneNumber, otp)) {
            // In a real application, you would send this OTP via SMS
            JOptionPane.showMessageDialog(this, 
                "OTP has been sent to " + phoneNumber + "\nFor testing, OTP is: " + otp);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to send OTP");
        }
    }
    
    private void verifyOTP() {
        String otp = otpField.getText().trim();
        if (otp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter OTP");
            return;
        }
        
        if (otpController.verifyOTP(phoneNumber, otp)) {
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
}