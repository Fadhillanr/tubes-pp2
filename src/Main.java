import view.LoginFrame;
import javax.swing.*;
import java.awt.*;
import view.RegistrationFrame;

public class Main {

    public static void main(String[] args) {
        try {
            // Set Look and Feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Run the application
            SwingUtilities.invokeLater(() -> {
                new RegistrationFrame().setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Failed to start application: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void showSplashScreen() {
        // Create splash screen frame
        JFrame splashFrame = new JFrame("E-Waste Management System");
        splashFrame.setUndecorated(true);
        splashFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create main panel with border
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 100, 0), 2));

        // Create content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Add title
        JLabel titleLabel = new JLabel("E-Waste Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 100, 0));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add(titleLabel, gbc);

        // Add version
        JLabel versionLabel = new JLabel("Version 1.0");
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridy = 1;
        contentPanel.add(versionLabel, gbc);

        // Add loading progress bar
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(200, 20));
        gbc.gridy = 2;
        contentPanel.add(progressBar, gbc);

        // Add loading text
        JLabel loadingLabel = new JLabel("Loading...");
        loadingLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridy = 3;
        contentPanel.add(loadingLabel, gbc);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        splashFrame.add(mainPanel);

        // Set size and center on screen
        splashFrame.setSize(400, 300);
        splashFrame.setLocationRelativeTo(null);
        splashFrame.setVisible(true);

        // Simulate loading time and start application
        new Timer(2000, e -> {
            splashFrame.dispose();
            showLoginFrame();
        }).start();
    }

    private static LoginFrame loginFrame;

    private static void showLoginFrame() {
        try {
            if (initializeDatabase()) {
                if (loginFrame == null) {
                    loginFrame = new LoginFrame();
                }
                loginFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Failed to connect to database. Please check your configuration.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Failed to start application: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private static boolean initializeDatabase() {
        try {
            // Test database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            java.sql.Connection conn = java.sql.DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/ewaste_db",
                    "root",
                    ""
            );
            conn.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
