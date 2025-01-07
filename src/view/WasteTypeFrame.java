package view;

import controller.WasteTypeController;
import controller.WasteCategoryController;
import model.WasteType;
import model.WasteCategory;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import java.text.DecimalFormat;
import java.text.ParseException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class WasteTypeFrame extends JFrame {

    private WasteTypeController typeController;
    private WasteCategoryController categoryController;
    private JTable typeTable;
    private DefaultTableModel tableModel;
    private JComboBox<WasteCategory> categoryComboBox;
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField weightField;
    private JTextField unitField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;

    public WasteTypeFrame() {
        typeController = new WasteTypeController();
        categoryController = new WasteCategoryController();
        initComponents();
        loadWasteTypes();
    }

    private void initComponents() {
        setTitle("Waste Type Management");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Category combo box
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Category:"), gbc);

        gbc.gridx = 1;
        categoryComboBox = new JComboBox<>();
        loadCategories();
        formPanel.add(categoryComboBox, gbc);

        // Name field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Description field
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        // Weight field
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Weight (e.g., 10 kg):"), gbc);

        gbc.gridx = 1;
        weightField = new JTextField(10);
        formPanel.add(weightField, gbc);

        // Unit field
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Total Items:"), gbc);

        gbc.gridx = 1;
        unitField = new JTextField(10);
        formPanel.add(unitField, gbc);

        // Buttons panel
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        addButton = new JButton("Add");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        formPanel.add(buttonPanel, gbc);

        // Table
        String[] columns = {"ID", "Category", "Name", "Description", "Weight", "Total Items", "Created At"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        typeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(typeTable);

        // Add components to main panel
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        addButton.addActionListener(e -> addWasteType());
        editButton.addActionListener(e -> editWasteType());
        deleteButton.addActionListener(e -> deleteWasteType());

        // Add selection listener to the table
        typeTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && typeTable.getSelectedRow() != -1) {
                    loadSelectedRowData();
                }
            }
        });

        add(mainPanel);
    }

    private void loadSelectedRowData() {
        int selectedRow = typeTable.getSelectedRow();
        if (selectedRow != -1) {
            WasteCategory selectedCategory = null;
            int categoryId = -1;

            Object categoryName = tableModel.getValueAt(selectedRow, 1);
            if (categoryName != null) {
                List<WasteCategory> categories = categoryController.getAllCategories();
                for (WasteCategory category : categories) {
                    if (category.getName().equals(categoryName)) {
                        selectedCategory = category;
                        categoryId = category.getId();
                        break;
                    }
                }
            }

            String name = (String) tableModel.getValueAt(selectedRow, 2);
            String description = (String) tableModel.getValueAt(selectedRow, 3);
            String weight = (String) tableModel.getValueAt(selectedRow, 4);
            String unit = (String) tableModel.getValueAt(selectedRow, 5);

            // Set values in form fields
            if (selectedCategory != null) {
                categoryComboBox.setSelectedItem(selectedCategory);
            }
            nameField.setText(name);
            descriptionArea.setText(description);
            weightField.setText(weight);
            unitField.setText(unit);
        }

    }

    private void loadCategories() {
        categoryComboBox.removeAllItems();
        List<WasteCategory> categories = categoryController.getAllCategories();
        if (categories != null) {
            for (WasteCategory category : categories) {
                categoryComboBox.addItem(category);
            }
        }
    }

    private void loadWasteTypes() {
        tableModel.setRowCount(0);
        List<WasteType> types = typeController.getAllWasteTypes();
        if (types != null) {
            for (WasteType type : types) {
                WasteCategory category = categoryController.getCategoryById(type.getCategoryId());
                Object[] row = {
                    type.getId(),
                    category != null ? category.getName() : "Unknown",
                    type.getName(),
                    type.getDescription(),
                    type.getWeight(),
                    type.getUnit(),
                    type.getCreatedAt()
                };
                tableModel.addRow(row);
            }
        }
    }

    private void addWasteType() {
        if (!validateForm()) {
            return;
        }

        WasteCategory selectedCategory = (WasteCategory) categoryComboBox.getSelectedItem();
        if (selectedCategory == null) {
            JOptionPane.showMessageDialog(this, "Please select a category");
            return;
        }

        try {
            String weight = weightField.getText().trim();
            String unit = unitField.getText();

            WasteType type = new WasteType(
                    selectedCategory.getId(),
                    nameField.getText().trim(),
                    descriptionArea.getText().trim(),
                    weight,
                    unit
            );

            if (typeController.create(type)) {
                JOptionPane.showMessageDialog(this, "Waste type added successfully");
                clearForm();
                loadWasteTypes();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add waste type");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid weight format" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void editWasteType() {
        int selectedRow = typeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a waste type to edit");
            return;
        }

        if (!validateForm()) {
            return;
        }

        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            WasteCategory selectedCategory = (WasteCategory) categoryComboBox.getSelectedItem();
            String weight = weightField.getText().trim();
            String unit = unitField.getText().trim();

            WasteType type = new WasteType(
                    selectedCategory.getId(),
                    nameField.getText().trim(),
                    descriptionArea.getText().trim(),
                    weight,
                    unit
            );
            type.setId(id);

            if (typeController.update(type)) {
                JOptionPane.showMessageDialog(this, "Waste type updated successfully");
                clearForm();
                loadWasteTypes();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update waste type");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid weight format" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteWasteType() {
        int selectedRow = typeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a waste type to delete");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this waste type?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (typeController.delete(id)) {
                JOptionPane.showMessageDialog(this, "Waste type deleted successfully");
                clearForm();
                loadWasteTypes();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete waste type");
            }
        }
    }

    private boolean validateForm() {
        if (categoryComboBox.getSelectedItem() == null
                || nameField.getText().trim().isEmpty()
                || descriptionArea.getText().trim().isEmpty()
                || weightField.getText().trim().isEmpty()
                || unitField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return false;
        }
        return true;

    }

    private void clearForm() {
        categoryComboBox.setSelectedIndex(0);
        nameField.setText("");
        descriptionArea.setText("");
        weightField.setText("");
        unitField.setText("");
        typeTable.clearSelection();
    }
}