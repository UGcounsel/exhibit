package Exhibition.gui;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.swing.filechooser.FileNameExtensionFilter;

import Exhibition.model.Participant;
import Exhibition.db.DatabaseManager;
import Exhibition.util.InputValidator;

public class ExhibitionGUI extends JFrame {
    private DatabaseManager dbManager;
    private JTextField regIdField, nameField, facultyField, projectField, contactField, emailField;
    private JLabel imageLabel;
    private JButton browseButton, registerButton, searchButton, updateButton, deleteButton, clearButton, exitButton;
    private String imagePath;

    private static final String IMAGE_FOLDER = "images";

    public ExhibitionGUI() {
        dbManager = new DatabaseManager();

        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
            UIManager.put("Component.arc", 18);
            UIManager.put("Button.arc", 20);
            UIManager.put("TextComponent.arc", 15);
            UIManager.put("Button.background", new Color(40, 90, 200));
            UIManager.put("Button.foreground", Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FlatAnimatedLafChange.showSnapshot();
        SwingUtilities.updateComponentTreeUI(this);
        FlatAnimatedLafChange.hideSnapshotWithAnimation();

        initializeComponents();
        setupUI();

        new File(IMAGE_FOLDER).mkdirs();
    }

    private void initializeComponents() {
        regIdField = new JTextField();
        nameField = new JTextField();
        facultyField = new JTextField();
        projectField = new JTextField();
        contactField = new JTextField();
        emailField = new JTextField();

        Dimension fieldSize = new Dimension(250, 35);
        for (JTextField field : new JTextField[]{regIdField, nameField, facultyField, projectField, contactField, emailField}) {
            field.setPreferredSize(fieldSize);
        }

        imageLabel = new JLabel("No image selected", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(280, 300));
        imageLabel.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 2, true));

        browseButton = new JButton("Browse Image");
        registerButton = new JButton("Register");
        searchButton = new JButton("Search");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        clearButton = new JButton("Clear");
        exitButton = new JButton("Exit");

        for (JButton btn : new JButton[]{registerButton, searchButton, updateButton, deleteButton, clearButton, exitButton, browseButton}) {
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
    }

    private void setupUI() {
        setTitle("🎓 Victoria University Exhibition Registration System");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Left side: Image panel
        JPanel imagePanel = new JPanel(new BorderLayout(10, 10));
        imagePanel.setBorder(BorderFactory.createTitledBorder("Project Image"));
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        imagePanel.add(browseButton, BorderLayout.SOUTH);

        // Right side: Form panel inside a card
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;

        String[] labels = {"Registration ID:", "Student Name:", "Faculty:", "Project Title:", "Contact Number:", "Email Address:"};
        JTextField[] fields = {regIdField, nameField, facultyField, projectField, contactField, emailField};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            formPanel.add(lbl, gbc);

            gbc.gridx = 1;
            formPanel.add(fields[i], gbc);
        }

        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(60, 60, 60), 1, true),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)
                )
        ));
        cardPanel.setBackground(new Color(30, 30, 30));
        cardPanel.add(formPanel, BorderLayout.CENTER);

        // Center: Split into left (image) and right (form card)
        JPanel centerPanel = new JPanel(new BorderLayout(20, 20));
        centerPanel.add(imagePanel, BorderLayout.WEST);
        centerPanel.add(cardPanel, BorderLayout.CENTER);

        // Bottom: Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.add(registerButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(exitButton);

        // Add actions
        browseButton.addActionListener(e -> browseImage());
        registerButton.addActionListener(e -> registerParticipant());
        searchButton.addActionListener(e -> searchParticipant());
        updateButton.addActionListener(e -> updateParticipant());
        deleteButton.addActionListener(e -> deleteParticipant());
        clearButton.addActionListener(e -> clearForm());
        exitButton.addActionListener(e -> System.exit(0));

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void browseImage() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String fileName = selectedFile.getName();

            try {
                Path destination = new File(IMAGE_FOLDER + File.separator + fileName).toPath();
                Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                imagePath = destination.toString();

                ImageIcon icon = new ImageIcon(imagePath);
                Image image = icon.getImage().getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(), Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(image));
                imageLabel.setText("");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error copying image: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

private void registerParticipant() {
    String regId = regIdField.getText().trim();
    String name = nameField.getText().trim();
    String faculty = facultyField.getText().trim();
    String project = projectField.getText().trim();
    String contact = contactField.getText().trim();
    String email = emailField.getText().trim();

    if (InputValidator.hasEmptyFields(regId, name, faculty, project, contact, email)) {
        JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        return;
    }

    if (!InputValidator.isValidEmail(email)) {
        JOptionPane.showMessageDialog(this, "Invalid email format.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        return;
    }

    Participant p = new Participant(regId, name, faculty, project, contact, email, imagePath);
    if (dbManager.addParticipant(p)) {
        JOptionPane.showMessageDialog(this, "Participant registered successfully.");
        clearForm();
    } else {
        JOptionPane.showMessageDialog(this, "Registration failed.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void searchParticipant() {
        String regId = regIdField.getText().trim();
        if (regId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Registration ID to search.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Participant p = dbManager.searchParticipant(regId);
        if (p != null) {
            nameField.setText(p.getName());
            facultyField.setText(p.getFaculty());
            projectField.setText(p.getProjectTitle());
            contactField.setText(p.getContact());
            emailField.setText(p.getEmail());
            imagePath = p.getImagePath();

            if (imagePath != null && !imagePath.isEmpty()) {
                File imgFile = new File(imagePath);
                if (imgFile.exists()) {
                    ImageIcon icon = new ImageIcon(imagePath);
                    Image img = icon.getImage().getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(), Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(img));
                    imageLabel.setText("");
                } else {
                    imageLabel.setIcon(null);
                    imageLabel.setText("Image not found");
                }
            } else {
                imageLabel.setIcon(null);
                imageLabel.setText("No image selected");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No participant found.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateParticipant() {
        String regId = regIdField.getText().trim();
        String name = nameField.getText().trim();
        String faculty = facultyField.getText().trim();
        String project = projectField.getText().trim();
        String contact = contactField.getText().trim();
        String email = emailField.getText().trim();

        if (regId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Registration ID to update.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Participant p = new Participant(regId, name, faculty, project, contact, email, imagePath);
        if (dbManager.updateParticipant(p)) {
            JOptionPane.showMessageDialog(this, "Participant updated successfully.");
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteParticipant() {
        String regId = regIdField.getText().trim();
        if (regId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Registration ID to delete.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this participant?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dbManager.deleteParticipant(regId)) {
                JOptionPane.showMessageDialog(this, "Participant deleted.");
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        regIdField.setText("");
        nameField.setText("");
        facultyField.setText("");
        projectField.setText("");
        contactField.setText("");
        emailField.setText("");
        imageLabel.setIcon(null);
        imageLabel.setText("No image selected");
        imagePath = "";
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExhibitionGUI().setVisible(true));
    }
}
