package view;

import model.User;
import model.WasteType;
import model.WasteCategory;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import controller.WasteTypeController;
import controller.WasteCategoryController;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

public class MainFrame extends JFrame {

    private User currentUser;
    private JMenuBar menuBar;
    private JPanel contentPanel;
    private JPanel dashboardPanel;
    private WasteTypeController typeController;
    private WasteCategoryController categoryController;
    private static LoginFrame loginFrame;

    public MainFrame(User user) {
        this.currentUser = user;
        typeController = new WasteTypeController();
        categoryController = new WasteCategoryController();
        initComponents();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    private void initComponents() {
        setTitle("E-Waste Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);

        // Create menu bar (akan diubah berdasarkan role)
        createMenuBar();

        // Create main content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + (currentUser != null ? currentUser.getUsername() : "Guest") + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Dashboard panel (akan diubah berdasarkan role)
        dashboardPanel = createDashboardPanel();
        contentPanel.add(dashboardPanel, BorderLayout.CENTER);

        add(contentPanel);
    }

    private void createMenuBar() {
        menuBar = new JMenuBar();

        if (currentUser != null && currentUser.getRole().equals("ADMIN")) {
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

            // Add menus to menu bar
            menuBar.add(fileMenu);
            menuBar.add(managementMenu);
        } else { // User role
            JMenu fileMenu = new JMenu("File");
            JMenuItem settingsItem = new JMenuItem("Settings");
            JMenuItem logoutItem = new JMenuItem("Logout");

            settingsItem.addActionListener(e -> openSettings());
            logoutItem.addActionListener(e -> logout());

            fileMenu.add(settingsItem);
            fileMenu.add(logoutItem);
            menuBar.add(fileMenu);
        }

        setJMenuBar(menuBar);
    }

    private JPanel createDashboardPanel() {
        if (currentUser != null && currentUser.getRole().equals("ADMIN")) {
            return createAdminDashboard();
        } else {
            return createUserDashboard();
        }
    }

    private JPanel createAdminDashboard() {
        JPanel dashboardPanel = new JPanel(new GridBagLayout());
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);

        JButton categoriesButton = createDashboardButton("Waste Categories");
        categoriesButton.addActionListener(e -> openWasteCategories());
        dashboardPanel.add(categoriesButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JButton typesButton = createDashboardButton("Waste Types");
        typesButton.addActionListener(e -> openWasteTypes());
        dashboardPanel.add(typesButton, gbc);

        return dashboardPanel;
    }

    private JPanel createUserDashboard() {
        JPanel dashboardPanel = new JPanel(new GridBagLayout());
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0; // Allow horizontal expansion
        gbc.weighty = 1.0; // Allow vertical expansion
        gbc.fill = GridBagConstraints.BOTH; // Fill both directions

        // Table
        String[] columns = {"Category", "Name", "Weight", "Total Items"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable wasteTable = new JTable(tableModel);
        loadTotalWasteTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(wasteTable);
        dashboardPanel.add(scrollPane, gbc);

        // Summary Panel
        JPanel summaryPanel = createSummaryPanel();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0;
        dashboardPanel.add(summaryPanel, gbc);

        return dashboardPanel;
    }

    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new GridBagLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        int totalCategories = categoryController.getAllCategories() != null ? categoryController.getAllCategories().size() : 0;
        int totalTypes = typeController.getAllWasteTypes() != null ? typeController.getAllWasteTypes().size() : 0;

        summaryPanel.add(new JLabel("Total Categories: "), gbc);
        gbc.gridx = 1;
        summaryPanel.add(new JLabel(String.valueOf(totalCategories)), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        summaryPanel.add(new JLabel("Total Waste Types: "), gbc);
        gbc.gridx = 1;
        summaryPanel.add(new JLabel(String.valueOf(totalTypes)), gbc);

        return summaryPanel;
    }

    private void loadTotalWasteTable(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        Map<String, List<WasteType>> typesPerCategory = getWasteTypesPerCategory();

        if (typesPerCategory != null) {
            for (Map.Entry<String, List<WasteType>> entry : typesPerCategory.entrySet()) {
                String categoryName = entry.getKey();
                List<WasteType> types = entry.getValue();
                for (WasteType type : types) {
                    Object[] row = {
                        categoryName,
                        type.getName(),
                        type.getWeight(),
                        type.getUnit()
                    };
                    tableModel.addRow(row);
                }
            }
        }
    }

    private String getWeightUnit(String weightText) {
        if (weightText.toLowerCase().endsWith("kg")) {
            return "kg";
        } else if (weightText.toLowerCase().endsWith("ton")) {
            return "ton";
        } else if (weightText.toLowerCase().endsWith("gram")) {
            return "gram";
        }
        return "kg";
    }

    private Map<String, List<WasteType>> getWasteTypesPerCategory() {
        List<WasteType> types = typeController.getAllWasteTypes();
        Map<String, List<WasteType>> typesPerCategory = new HashMap<>();
        if (types != null) {
            for (WasteType type : types) {
                WasteCategory category = categoryController.getCategoryById(type.getCategoryId());
                if (category != null) {
                    typesPerCategory.computeIfAbsent(category.getName(), k -> new ArrayList<>()).add(type);
                } else {
                    System.err.println("Invalid category id for waste type id:" + type.getId());
                }
            }
        }
        return typesPerCategory;
    }

//    private JPanel createProfilePanel() {
//        //kita tidak memerlukan nya lagi
//        return new JPanel();
//    }
    private JButton createDashboardButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        return button;
    }

    private ImageIcon loadIcon(String iconPath) {
        try {
            java.net.URL imgURL = getClass().getResource(iconPath);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(new ImageIcon(imgURL).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                return icon;
            } else {
                System.err.println("Error load image with path " + iconPath + " url null");
                return null;
            }

        } catch (Exception e) {
            // Icon not found, continue without icon
            System.err.println("Error load image with path " + iconPath + " exception " + e.getMessage());
            return null;
        }
    }

    private void openSettings() {
        if (currentUser != null) {
            ProfileSettingsDialog settingsDialog = new ProfileSettingsDialog(this, currentUser);
            settingsDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "You are not Logged In!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openWasteCategories() {
        WasteCategoryFrame categoryFrame = new WasteCategoryFrame();
        categoryFrame.setVisible(true);
    }

    private void openWasteTypes() {
        WasteTypeFrame typeFrame = new WasteTypeFrame();
        typeFrame.setVisible(true);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            if (loginFrame == null) {
                loginFrame = new LoginFrame();
            }
            LoginFrame.getMainFrame().dispose();
            loginFrame.setVisible(true);
        }
    }
}
