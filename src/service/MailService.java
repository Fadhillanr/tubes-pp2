package service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import javax.swing.JOptionPane;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MailService {

    public boolean sendOTP(String email, String otpCode) {
        // Define SMTP Properties
        final String username = "pradithaakaka@gmail.com"; // Replace with your email
        final String password = "qkaj dqax lbaw jpji"; // Replace with your email password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com"); // Replace with your SMTP host
        props.put("mail.smtp.port", "587"); // Replace with your SMTP port

        // Tambahkan properti SSL/TLS
        props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");

        // Create session
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Your OTP Code");


            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiredAt = now.plusMinutes(5);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedExpiredAt = expiredAt.format(formatter);


           String emailBody = "Your OTP for e-Waste system registration is: " + otpCode + "\n"
                     + "This OTP will expire at: " + formattedExpiredAt + "\n"
                    + "Please use it within 5 minutes.";
           message.setText(emailBody);


            // Send message
            Transport.send(message);
            System.out.println("OTP Sent to " + email);
            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to send OTP code via email, please check configuration " + e.getMessage());
            return false;
        }
    }
}