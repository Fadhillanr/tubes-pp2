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
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame extends JFrame {

    private User currentUser;
    private JMenuBar menuBar;
    private JPanel contentPanel;
    private JPanel dashboardPanel;
    private WasteTypeController typeController;
    private WasteCategoryController categoryController;
    private static LoginFrame loginFrame;
    private JTable wasteTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    // Warna dan font yang konsisten
    private static final Color PRIMARY_COLOR = new Color(50, 157, 156); // #329D9C
    private static final Color BACKGROUND_COLOR = new Color(207, 244, 210); // #CFF4D2
    private static final Color TABLE_HEADER_COLOR = new Color(30, 100, 100); // Warna header tabel lebih gelap
    private static final Color TABLE_CELL_COLOR = new Color(240, 240, 240); // Warna cell tabel agak abu-abu
    private static final Color TABLE_CELL_SELECTED_COLOR = new Color(200, 200, 200); // Warna sel saat diklik
    private static final Color BUTTON_TEXT_COLOR = new Color(50, 50, 50); // Dark gray for button text
    private static final Font TABLE_FONT = new Font("Arial", Font.PLAIN, 14); // Font untuk tabel
    private static final Font FOOTER_FONT = new Font("Arial", Font.PLAIN, 12); // Font untuk footer


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
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Create menu bar
        createMenuBar();

        // Create main content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.setBackground(BACKGROUND_COLOR);

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + (currentUser != null ? currentUser.getUsername() : "Guest") + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setForeground(PRIMARY_COLOR);
        contentPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Dashboard panel
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
            return createUserDashboard(); // Memanggil metode yang benar
        }
    }


     private JPanel createAdminDashboard() {
        JPanel dashboardPanel = new JPanel(new GridBagLayout());
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        dashboardPanel.setBackground(BACKGROUND_COLOR);

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

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton viewWasteButton = createDashboardButton("View Total Waste");
        viewWasteButton.addActionListener(e -> displayWasteTable());
        dashboardPanel.add(viewWasteButton, gbc);
        return dashboardPanel;
    }
    
    private JPanel createUserDashboard() {
        JPanel dashboardPanel = new JPanel(new GridBagLayout());
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        dashboardPanel.setBackground(BACKGROUND_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        // Table
        String[] columns = {"Category", "Name", "Weight", "Unit"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        wasteTable = new JTable(tableModel);
        customizeTableAppearance(wasteTable);
        scrollPane = new JScrollPane(wasteTable);
        dashboardPanel.add(scrollPane, gbc);
        loadTotalWasteTable(tableModel);

        // Summary Panel
        JPanel summaryPanel = createSummaryPanel();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0;
        dashboardPanel.add(summaryPanel, gbc);

        return dashboardPanel;
    }


    private void displayWasteTable() {
       if (dashboardPanel != null) {
            dashboardPanel.removeAll();

            // GridBagConstraints untuk mengatur layout
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;

           // Table
           String[] columns = {"Category", "Name", "Weight", "Unit"};
            tableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

           wasteTable = new JTable(tableModel);
           customizeTableAppearance(wasteTable);
           scrollPane = new JScrollPane(wasteTable);
           dashboardPanel.add(scrollPane, gbc);
           loadTotalWasteTable(tableModel);

            // Summary Panel
            JPanel summaryPanel = createSummaryPanel();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weighty = 0;
            dashboardPanel.add(summaryPanel, gbc);

            dashboardPanel.revalidate();
            dashboardPanel.repaint();
        }
    }

    private void customizeTableAppearance(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER_COLOR);
        header.setFont(TABLE_FONT);

       DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
               if (isSelected) {
                    c.setBackground(TABLE_CELL_SELECTED_COLOR);
                } else {
                    c.setBackground(TABLE_CELL_COLOR);
                }
                return c;
            }
        };
       table.setDefaultRenderer(Object.class, cellRenderer);
       table.setSelectionBackground(TABLE_CELL_SELECTED_COLOR);
        table.setRowHeight(25);
        table.setFont(TABLE_FONT);
   }

   private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new GridBagLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
        summaryPanel.setBackground(BACKGROUND_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel totalCategoriesLabel = new JLabel("Total Categories: ");
        totalCategoriesLabel.setFont(FOOTER_FONT);
        summaryPanel.add(totalCategoriesLabel, gbc);

       gbc.gridx = 1;
        JLabel totalCategoriesValue = new JLabel(String.valueOf(categoryController.getAllCategories() != null ? categoryController.getAllCategories().size() : 0));
        totalCategoriesValue.setFont(FOOTER_FONT);
        summaryPanel.add(totalCategoriesValue, gbc);

       gbc.gridx = 0;
       gbc.gridy = 1;
       JLabel totalWasteTypesLabel = new JLabel("Total Waste Types: ");
       totalWasteTypesLabel.setFont(FOOTER_FONT);
        summaryPanel.add(totalWasteTypesLabel, gbc);


       gbc.gridx = 1;
        JLabel totalWasteTypesValue = new JLabel(String.valueOf(typeController.getAllWasteTypes() != null ? typeController.getAllWasteTypes().size() : 0));
       totalWasteTypesValue.setFont(FOOTER_FONT);
        summaryPanel.add(totalWasteTypesValue, gbc);

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

    private JButton createDashboardButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(BUTTON_TEXT_COLOR); // Ensure text color is dark gray
        button.setPreferredSize(new Dimension(150, 40)); // Set preferred size
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