package view;

import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {
    private User currentUser;
    private JMenuBar menuBar;
    private JPanel contentPanel;
    
    public MainFrame(User user) {
        this.currentUser = user;
        initComponents();
    }
    
    private void initComponents() {
        setTitle("E-Waste Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);
        
        // Create menu bar
        createMenuBar();
        
        // Create main content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(welcomeLabel, BorderLayout.NORTH);
        
        // Dashboard panel
        JPanel dashboardPanel = createDashboardPanel();
        contentPanel.add(dashboardPanel, BorderLayout.CENTER);
        
        add(contentPanel);
    }
    
    private void createMenuBar() {
        menuBar = new JMenuBar();
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem settingsItem = new JMenuItem("Settings");
        JMenuItem logoutItem = new JMenuItem("Logout");
        
        settingsItem.addActionListener(e -> openSettings());
        logoutItem.addActionListener(e -> logout());
        
        fileMenu.add(settingsItem);
        fileMenu.add(new JSeparator());
        fileMenu.add(logoutItem);
        
        // Management Menu
        JMenu managementMenu = new JMenu("Management");
        JMenuItem categoriesItem = new JMenuItem("Waste Categories");
        JMenuItem typesItem = new JMenuItem("Waste Types");
        
        categoriesItem.addActionListener(e -> openWasteCategories());
        typesItem.addActionListener(e -> openWasteTypes());
        
        managementMenu.add(categoriesItem);
        managementMenu.add(typesItem);
        
        // Reports Menu
        JMenu reportsMenu = new JMenu("Reports");
        JMenuItem summaryItem = new JMenuItem("Summary Report");
        
        summaryItem.addActionListener(e -> openSummaryReport());
        
        reportsMenu.add(summaryItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(managementMenu);
        menuBar.add(reportsMenu);
        
        setJMenuBar(menuBar);
    }
    
    private JPanel createDashboardPanel() {
        JPanel dashboardPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Quick access buttons
        JButton categoriesButton = createDashboardButton("Waste Categories", "/icons/categories.png");
        JButton typesButton = createDashboardButton("Waste Types", "/icons/types.png");
        JButton reportButton = createDashboardButton("Reports", "/icons/reports.png");
        JButton settingsButton = createDashboardButton("Settings", "/icons/settings.png");
        
        categoriesButton.addActionListener(e -> openWasteCategories());
        typesButton.addActionListener(e -> openWasteTypes());
        reportButton.addActionListener(e -> openSummaryReport());
        settingsButton.addActionListener(e -> openSettings());
        
        dashboardPanel.add(categoriesButton);
        dashboardPanel.add(typesButton);
        dashboardPanel.add(reportButton);
        dashboardPanel.add(settingsButton);
        
        return dashboardPanel;
    }
    
    private JButton createDashboardButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            button.setIcon(icon);
        } catch (Exception e) {
            // Icon not found, continue without icon
        }
        
        return button;
    }
    
    private void openSettings() {
        ProfileSettingsDialog settingsDialog = new ProfileSettingsDialog(this, currentUser);
        settingsDialog.setVisible(true);
    }
    
    private void openWasteCategories() {
        WasteCategoryFrame categoryFrame = new WasteCategoryFrame();
        categoryFrame.setVisible(true);
    }
    
    private void openWasteTypes() {
        WasteTypeFrame typeFrame = new WasteTypeFrame();
        typeFrame.setVisible(true);
    }
    
    private void openSummaryReport() {
        // Implement summary report
        JOptionPane.showMessageDialog(this,
            "Summary report feature coming soon!",
            "Information",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            this.dispose();
        }
    }
}