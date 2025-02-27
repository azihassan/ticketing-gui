package io.hahn.ticket;

import io.hahn.ApiClient;
import io.hahn.ticket.dto.Category;
import io.hahn.ticket.dto.Priority;
import io.hahn.ticket.dto.Status;
import io.hahn.ticket.dto.TicketCreate;

import javax.swing.*;
import java.awt.*;

import static javax.swing.SwingUtilities.invokeLater;

class TicketCreationDialog extends JDialog {

    private final JTextField titleField;
    private final JComboBox<String> priorityComboBox;
    private final JTextArea descriptionArea;
    private final JComboBox<String> categoryComboBox;
    private final ApiClient apiClient;
    private final Runnable onTicketCreated;
    private final JButton createButton;

    public TicketCreationDialog(ApiClient apiClient, Runnable onTicketCreated, Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        this.apiClient = apiClient;
        this.onTicketCreated = onTicketCreated;
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST; // Align labels to the left

        // ... (Add form fields to the dialog - Title, Description, Priority, Category)
        JLabel titleLabel = new JLabel("Title:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(titleLabel, gbc);

        titleField = new JTextField(30);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(titleField, gbc);

        JLabel descriptionLabel = new JLabel("Description:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(descriptionLabel, gbc);

        // 5 rows, 30 columns
        descriptionArea = new JTextArea(5, 30);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(descriptionScrollPane, gbc);

        JLabel priorityLabel = new JLabel("Priority:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(priorityLabel, gbc);

        priorityComboBox = new JComboBox<>(new String[]{"LOW", "MEDIUM", "HIGH"});
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(priorityComboBox, gbc);

        JLabel categoryLabel = new JLabel("Category:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(categoryLabel, gbc);

        categoryComboBox = new JComboBox<>(new String[]{"NETWORK", "HARDWARE", "SOFTWARE", "OTHER"});
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(categoryComboBox, gbc);

        createButton = new JButton("Create");
        createButton.addActionListener(e -> createTicket());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> setVisible(false));

        JPanel dialogButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        dialogButtonPanel.add(createButton);
        dialogButtonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Span two columns
        add(dialogButtonPanel, gbc);

        pack();
        setLocationRelativeTo(null); // Center the dialog
    }

    public void resetFields() {
        titleField.setText("");
        descriptionArea.setText("");
        priorityComboBox.setSelectedIndex(0);
        categoryComboBox.setSelectedIndex(0);
    }

    private void createTicket() {
        String title = titleField.getText();
        String description = descriptionArea.getText();
        String priority = (String) priorityComboBox.getSelectedItem();
        String category = (String) categoryComboBox.getSelectedItem();
        createButton.setEnabled(false);

        new Thread(() -> {
            try {
                TicketCreate ticketCreate = new TicketCreate();
                ticketCreate.setTitle(title);
                ticketCreate.setDescription(description);
                ticketCreate.setPriority(Priority.fromValue(priority));
                ticketCreate.setCategory(Category.fromValue(category));
                ticketCreate.setStatus(Status.NEW); // Initial status

                apiClient.createTicket(ticketCreate); // Call the API
                invokeLater(() -> {
                    setVisible(false); // Hide the dialog
                });
                onTicketCreated.run();
                invokeLater(() -> JOptionPane.showMessageDialog(this, "Ticket created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE));

            } catch (Exception ex) {
                invokeLater(() -> JOptionPane.showMessageDialog(this, "Error creating ticket: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
                ex.printStackTrace();
            } finally {
                invokeLater(() -> createButton.setEnabled(true));
            }
        }).start();
    }
}
