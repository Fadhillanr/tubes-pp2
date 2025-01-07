package view;

import controller.WasteCategoryController;
import model.WasteCategory;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class WasteCategoryFrame extends JFrame {
    private WasteCategoryController categoryController;
    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    
    public WasteCategoryFrame() {
        categoryController = new WasteCategoryController();
        initComponents();
        loadCategories();
    }
    
    private void initComponents() {
        setTitle("Waste Category Management");
        setSize(800, 600);
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
        
        // Name field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        
        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);
        
        // Description field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Description:"), gbc);
        
        gbc.gridx = 1;
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        formPanel.add(new JScrollPane(descriptionArea), gbc);
        
        // Buttons panel
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        addButton = new JButton("Add");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        formPanel.add(buttonPanel, gbc);
        
        // Table
        String[] columns = {"ID", "Name", "Description", "Created By", "Created At"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        categoryTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(categoryTable);
        
        // Add components to main panel
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add action listeners
        addButton.addActionListener(e -> addCategory());
        editButton.addActionListener(e -> editCategory());
        deleteButton.addActionListener(e -> deleteCategory());
        
        add(mainPanel);
    }
    
    private void loadCategories() {
        tableModel.setRowCount(0);
        List<WasteCategory> categories = categoryController.getAllCategories();
        
        for (WasteCategory category : categories) {
            Object[] row = {
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getCreatedBy(),
                category.getCreatedAt()
            };
            tableModel.addRow(row);
        }
    }
    
    private void addCategory() {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();
        
        if (name.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return;
        }
        
        WasteCategory category = new WasteCategory(name, description);
        category.setCreatedBy("current_user"); // Should be replaced with actual logged-in user
        
        if (categoryController.create(category)) {
            JOptionPane.showMessageDialog(this, "Category added successfully");
            clearForm();
            loadCategories();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add category");
        }
    }
    
    private void editCategory() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a category to edit");
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();
        
        if (name.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return;
        }
        
        WasteCategory category = new WasteCategory(name, description);
        category.setId(id);
        
        if (categoryController.update(category)) {
            JOptionPane.showMessageDialog(this, "Category updated successfully");
            clearForm();
            loadCategories();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update category");
        }
    }
    
    private void deleteCategory() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a category to delete");
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this category?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (categoryController.delete(id)) {
                JOptionPane.showMessageDialog(this, "Category deleted successfully");
                clearForm();
                loadCategories();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete category");
            }
        }
    }
    
    private void clearForm() {
        nameField.setText("");
        descriptionArea.setText("");
        categoryTable.clearSelection();
    }
}