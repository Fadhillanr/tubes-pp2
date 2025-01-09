package view;

import controller.UserController;
import model.User;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ProfileSettingsDialog extends JDialog {

    private static final int IMAGE_SIZE = 150;
    private static final Color PRIMARY_COLOR = new Color(50, 157, 156); // #329D9C
    private static final Color BUTTON_COLOR = new Color(123, 228, 149); // Lighter shade for visibility
    private static final Color BUTTON_HOVER_COLOR = new Color(220, 220, 220);
    private static final Color ERROR_COLOR = new Color(220, 53, 69);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color BACKGROUND_COLOR = new Color(207, 244, 210); // #CFF4D2

    private UserController userController;
    private User currentUser ;
    private File selectedImageFile;

    // UI Components
    private JLabel profileImageLabel;
    private JProgressBar uploadProgressBar;
    private JLabel statusLabel;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressArea;
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;

    public ProfileSettingsDialog(JFrame parent, User user) {
        super(parent, "Profile Settings", true);
        this.userController = new UserController();
        this.currentUser  = user;
        initComponents();
        loadProfileImage();
    }

    private void initComponents() {
        setSize(500, 650);
        setLocationRelativeTo(getOwner());
        setMinimumSize(new Dimension(450, 600));
        getContentPane().setBackground(BACKGROUND_COLOR);

        JTabbedPane tabbedPane = createTabbedPane();
        add(tabbedPane);
    }

    private JTabbedPane createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        tabbedPane.addTab("Profile", createProfilePanel());
        tabbedPane.addTab("Password", createPasswordPanel());
        return tabbedPane;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(createProfileImagePanel(), gbc);

        gbc.gridy = 1;
        panel.add(createProfileInfoPanel(), gbc);

        gbc.gridy = 2;
        panel.add(createButtonPanel(), gbc);

        gbc.gridy = 3;
        panel.add(createStatusPanel(), gbc);

        return panel;
    }

    private JPanel createProfileImagePanel() {
        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS));
        imagePanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        imagePanel.setBackground(BACKGROUND_COLOR);

        profileImageLabel = new JLabel();
        profileImageLabel.setPreferredSize(new Dimension(IMAGE_SIZE, IMAGE_SIZE));
        profileImageLabel.setMaximumSize(new Dimension(IMAGE_SIZE, IMAGE_SIZE));
        profileImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        profileImageLabel.setBorder(new CompoundBorder(
                new LineBorder(PRIMARY_COLOR, 2),
                new EmptyBorder(5, 5, 5, 5)
        ));

        JPanel controlPanel = new JPanel();
        controlPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlPanel.setBackground(BACKGROUND_COLOR);

        JButton changeButton = createStyledButton("Change Picture");
        JButton removeButton = createStyledButton("Remove Picture");

        changeButton.addActionListener(e -> selectProfileImage());
        removeButton.addActionListener(e -> removeProfileImage());

        controlPanel.add(changeButton);
        controlPanel.add(Box .createHorizontalStrut(10));
        controlPanel.add(removeButton);

        imagePanel.add(profileImageLabel);
        imagePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        imagePanel.add(controlPanel);

        return imagePanel;
    }

    private JPanel createProfileInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Personal Information"));
        panel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        addFormField(panel, "Username:", createReadOnlyField(currentUser .getUsername()), gbc, 0);

        emailField = new JTextField(20);
        emailField.setText(currentUser .getEmail());
        addFormField(panel, "Email:", emailField, gbc, 1);

        phoneField = new JTextField(20);
        phoneField.setText(currentUser .getPhoneNumber());
        addFormField(panel, "Phone:", phoneField, gbc, 2);

        addressArea = new JTextArea(3, 20);
        addressArea.setText(currentUser .getAddress());
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(addressArea);
        addFormField(panel, "Address:", scrollPane, gbc, 3);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(BACKGROUND_COLOR);
        JButton saveButton = createStyledButton("Save Changes");
        saveButton.addActionListener(e -> saveProfile());
        JButton deleteAccountButton = createStyledButton("Delete Account");
        deleteAccountButton.addActionListener(e -> deleteAccount());
        panel.add(saveButton);
        panel.add(deleteAccountButton);
        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        uploadProgressBar = new JProgressBar(0, 100);
        uploadProgressBar.setStringPainted(true);
        uploadProgressBar.setVisible(false);

        statusLabel = new JLabel("");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(uploadProgressBar, BorderLayout.NORTH);
        panel.add(statusLabel, BorderLayout.CENTER);

        return panel;
    }

    private void selectProfileImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Image files", "jpg", "jpeg", "png", "gif"
        ));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            try {
                if (currentUser .validateProfileImage(selectedImageFile)) {
                    updateProfileImagePreview();
                    showStatus("Image selected successfully", true);
                } else {
                    showStatus("Invalid image file. Please select a valid image.", false);
                    selectedImageFile = null;
                }
            } catch (Exception e) {
                showStatus("Error processing image: " + e.getMessage(), false);
                selectedImageFile = null;
            }
        }
    }

    private void updateProfileImagePreview() {
        if (selectedImageFile != null) {
            SwingWorker<ImageIcon, Void> worker = new SwingWorker<ImageIcon, Void>() {
                @Override
                protected ImageIcon doInBackground() throws Exception {
                    try {
                        BufferedImage img = ImageIO.read(selectedImageFile);
                        if (img != null) {
                            Image scaledImg = img.getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_SMOOTH);
                            return new ImageIcon(scaledImg);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        showStatus("Error previewing image", false);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        ImageIcon icon = get();
                        if (icon != null) {
                            profileImageLabel.setIcon(icon);
                        } else {
                            setDefaultProfileImage();
                        }
                    } catch (Exception e) {
                        profileImageLabel.setIcon(null);
                        profileImageLabel.setText("No Image");
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
        }
    }

    private void removeProfileImage() {
        int response = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove your profile picture?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION
        );

        if (response == JOptionPane.YES_OPTION) {
            selectedImageFile = null;
            currentUser .setProfileImagePath(null);
            setDefaultProfileImage();
            showStatus("Profile picture removed", true);
        }
    }

    private void setDefaultProfileImage() {
        try {
            java.net.URL imgURL = getClass().getResource("/icons/user.png");
            if (imgURL != null) {
                BufferedImage defaultImg = ImageIO.read(imgURL);
                if (defaultImg != null) {
                    Image scaledImg = defaultImg.getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_SMOOTH);
                    profileImageLabel.setIcon(new ImageIcon(scaledImg));
                } else {
                    profileImageLabel.setIcon(null);
                    profileImageLabel.setText("No Image");
                }
            } else {
                profileImageLabel.setIcon(null);
                profileImageLabel.setText("No Image");
            }
        } catch (IOException e) {
            e.printStackTrace();
            profileImageLabel.setIcon(null);
            profileImageLabel.setText("No Image");
        }
    }

    private void saveProfile() {
        currentUser .setEmail(emailField.getText().trim());
        currentUser .setPhoneNumber(phoneField.getText().trim());
        currentUser .setAddress(addressArea.getText().trim());

        uploadProgressBar.setVisible(true);
        uploadProgressBar.setValue(0);

        SwingWorker<Boolean, Integer> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                for (int i = 0; i <= 100; i += 10) {
                    Thread.sleep(100);
                    publish(i);
                }
                return userController.updateProfile(currentUser , selectedImageFile);
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                uploadProgressBar.setValue(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        showStatus("Profile updated successfully", true);
                        selectedImageFile = null;
                    } else {
                        showStatus("Failed to update profile", false);
                    }
                } catch (Exception e) {
                    showStatus("Error: " + e.getMessage(), false);
                }
                uploadProgressBar.setVisible(false);
            }
        };

        worker.execute();
    }

    private void showStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setForeground(success ? SUCCESS_COLOR : ERROR_COLOR);
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

    private JTextField createReadOnlyField(String text) {
        JTextField field = new JTextField(text);
        field.setEditable(false);
        field.setBackground(new Color(240, 240, 240));
        return field;
    }

    private void addFormField(JPanel panel, String label, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
        gbc.weightx = 0.0;
    }

    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Change Password"));
        panel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Old Password
        JLabel oldPassLabel = new JLabel("Old Password:");
        oldPassLabel.setFont(new Font("Arial", Font.BOLD, 14));
 oldPassLabel.setForeground(new Color(86, 197, 150)); // #56c596
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(oldPassLabel, gbc);

        oldPasswordField = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(oldPasswordField, gbc);

        // New Password
        JLabel newPassLabel = new JLabel("New Password:");
        newPassLabel.setFont(new Font("Arial", Font.BOLD, 14));
        newPassLabel.setForeground(new Color(86, 197, 150)); // #56c596
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(newPassLabel, gbc);

        newPasswordField = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(newPasswordField, gbc);

        // Confirm New Password
        JLabel confirmPassLabel = new JLabel("Confirm New Password:");
        confirmPassLabel.setFont(new Font("Arial", Font.BOLD, 14));
        confirmPassLabel.setForeground(new Color(86, 197, 150)); // #56c596
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(confirmPassLabel, gbc);

        confirmPasswordField = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(confirmPasswordField, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        JButton changePassButton = createStyledButton("Change Password");
        changePassButton.addActionListener(e -> handleChangePassword());
        buttonPanel.add(changePassButton);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private void handleChangePassword() {
        String oldPassword = new String(oldPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showStatus("Please fill in all password fields", false);
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            showStatus("New password and confirm password do not match", false);
            return;
        }
        if (newPassword.equals(oldPassword)) {
            showStatus("New password must be different from old password", false);
            return;
        }
        // Check if old password matches the current user password
        User user = userController.login(currentUser .getUsername(), oldPassword);
        if (user == null) {
            showStatus("Old password incorrect", false);
            return;
        }
        // Implement password reset here
        boolean passwordChanged = userController.resetPassword(currentUser .getUsername(), newPassword);
        if (passwordChanged) {
            showStatus("Password changed successfully", true);
            dispose();
            LoginFrame.getMainFrame().dispose();
            new LoginFrame().setVisible(true);
        } else {
            showStatus("Failed to change password. Please try again", false);
        }
    }

    private void deleteAccount() {
        int response = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete your account?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (response == JOptionPane.YES_OPTION) {
            if (userController.deleteAccount(currentUser .getId())) {
                JOptionPane.showMessageDialog(this, "Account deleted successfully, please log in again");
                dispose();
                LoginFrame.getMainFrame().dispose();
                new LoginFrame().setVisible(true);
            } else {
                showStatus("Failed to delete account. Please try again", false);
            }
        }
    }

    private void loadProfileImage() {
        SwingWorker<ImageIcon, Void> worker = new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                String imagePath = currentUser .getProfileImagePath();
                if (imagePath != null && !imagePath.isEmpty()) {
                    File imageFile = new File(imagePath);
                    if (imageFile.exists() && imageFile.isFile()) {
                        try {
                            BufferedImage img = ImageIO.read(imageFile);
                            if (img != null) {
                                Image scaledImg = img.getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_SMOOTH);
                                return new ImageIcon(scaledImg);
                            }
                        } catch (IOException e) {
                            System.err.println("Failed to load user profile image: " + e.getMessage());
                            return null;
                        }
                    }
                }
                java.net.URL imgURL = getClass().getResource("/icons/user.png");
                if (imgURL != null) {
                    try {
                        BufferedImage defaultImg = ImageIO.read(imgURL);
                        if (defaultImg != null) {
                            Image scaledImg = defaultImg.getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_SMOOTH);
                            return new ImageIcon(scaledImg);
                        }
                    } catch (IOException e) {
                        System.err.println("Failed to load default profile image: " + e.getMessage());
                        return null;
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    ImageIcon icon = get();
                    if (icon != null) {
                        profileImageLabel.setIcon(icon);
                    } else {
                        setDefaultProfileImage();
                    }
                } catch (Exception e) {
                    profileImageLabel.setIcon(null);
                    profileImageLabel.setText("No Image");
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
}