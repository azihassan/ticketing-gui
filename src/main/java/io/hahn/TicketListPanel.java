package io.hahn;

import io.hahn.dto.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TicketListPanel extends JPanel {

    private final JTable ticketTable;
    private final DefaultTableModel tableModel;
    private final JButton createTicketButton;
    private final JDialog createTicketDialog;
    private final JTextField titleField;
    private final JTextArea descriptionArea;
    private final JComboBox<String> priorityComboBox;
    private final JComboBox<String> categoryComboBox;
    private final ApiClient apiClient;

    public TicketListPanel(ApiClient apiClient) {
        this.apiClient = apiClient;
        setLayout(new BorderLayout());

        // Create the table model
        tableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Priority", "Status", "Created At"}, 0);
        ticketTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Set up the table (e.g., column widths, row height)
        ticketTable.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        ticketTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Title
        // ... set widths for other columns

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(ticketTable);
        add(scrollPane, BorderLayout.CENTER);

        // Load the tickets (in a separate thread)
        loadTickets();

        // Create the "Create Ticket" button
        createTicketButton = new JButton("Create New Ticket");
        createTicketButton.addActionListener(e -> showCreateTicketDialog());

        // Add the button to a panel (e.g., at the bottom)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Align to right
        buttonPanel.add(createTicketButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Create the "Create Ticket" dialog (but don't show it yet)
        createTicketDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Create Ticket", true); // Modal
        createTicketDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST; // Align labels to the left

        // ... (Add form fields to the dialog - Title, Description, Priority, Category)
        JLabel titleLabel = new JLabel("Title:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        createTicketDialog.add(titleLabel, gbc);

        titleField = new JTextField(30);
        gbc.gridx = 1;
        gbc.gridy = 0;
        createTicketDialog.add(titleField, gbc);

        JLabel descriptionLabel = new JLabel("Description:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        createTicketDialog.add(descriptionLabel, gbc);

        descriptionArea = new JTextArea(5, 30); // 5 rows, 30 columns
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        gbc.gridx = 1;
        gbc.gridy = 1;
        createTicketDialog.add(descriptionScrollPane, gbc);

        JLabel priorityLabel = new JLabel("Priority:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        createTicketDialog.add(priorityLabel, gbc);

        priorityComboBox = new JComboBox<>(new String[]{"LOW", "MEDIUM", "HIGH"});
        gbc.gridx = 1;
        gbc.gridy = 2;
        createTicketDialog.add(priorityComboBox, gbc);

        JLabel categoryLabel = new JLabel("Category:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        createTicketDialog.add(categoryLabel, gbc);

        categoryComboBox = new JComboBox<>(new String[]{"NETWORK", "HARDWARE", "SOFTWARE", "OTHER"});
        gbc.gridx = 1;
        gbc.gridy = 3;
        createTicketDialog.add(categoryComboBox, gbc);


        // ... (Add "Create" and "Cancel" buttons to the dialog)
        JButton createButton = new JButton("Create");
        createButton.addActionListener(e -> createTicket());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> createTicketDialog.setVisible(false));

        ticketTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) { // Double-click
                    int row = ticketTable.getSelectedRow();
                    if (row >= 0) {
                        Long ticketId = (Long) tableModel.getValueAt(row, 0); // Get ticket ID
                        try {
                            Ticket ticket = apiClient.getTicketById(ticketId);
                            if (ticket != null) {
                                openTicketDetails(ticket);
                            } else {
                                JOptionPane.showMessageDialog(TicketListPanel.this, "Ticket not found.",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(TicketListPanel.this, "Error fetching ticket details: " + ex.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        JPanel dialogButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        dialogButtonPanel.add(createButton);
        dialogButtonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Span two columns
        createTicketDialog.add(dialogButtonPanel, gbc);

        createTicketDialog.pack();
        createTicketDialog.setLocationRelativeTo(null); // Center the dialog
    }

    public void loadTickets() {
        new Thread(() -> {
            try {
                TicketPage ticketPage = apiClient.getTickets(null, null); // Get all tickets (no filters initially)
                List<Ticket> tickets = ticketPage.getContent(); // Access the ticket content

                // Update the table model on the EDT
                SwingUtilities.invokeLater(() -> {
                    tableModel.setRowCount(0); // Clear existing data
                    for (Ticket ticket : tickets) {
                        tableModel.addRow(new Object[]{
                                ticket.getId(), ticket.getTitle(), ticket.getPriority(), ticket.getStatus(), ticket.getCreatedAt()
                        });
                    }
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error loading tickets: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                });
            }
        }).start();
    }

    private void showCreateTicketDialog() {
        // Reset the form fields before showing the dialog
        titleField.setText("");
        descriptionArea.setText("");
        priorityComboBox.setSelectedIndex(0);
        categoryComboBox.setSelectedIndex(0);

        createTicketDialog.setVisible(true);
    }

    private void openTicketDetails(Ticket ticket) {
        JFrame ticketDetailsFrame = new JFrame("Ticket Details - " + ticket.getId());
        TicketDetailsPanel ticketDetailsPanel = new TicketDetailsPanel(apiClient, ticket);
        ticketDetailsFrame.setContentPane(ticketDetailsPanel);
        ticketDetailsFrame.pack();
        ticketDetailsFrame.setLocationRelativeTo(null);
        ticketDetailsFrame.setVisible(true);
    }

    private void createTicket() {
        String title = titleField.getText();
        String description = descriptionArea.getText();
        String priority = (String) priorityComboBox.getSelectedItem();
        String category = (String) categoryComboBox.getSelectedItem();

        try {
            TicketCreate ticketCreate = new TicketCreate();
            ticketCreate.setTitle(title);
            ticketCreate.setDescription(description);
            ticketCreate.setPriority(Priority.fromValue(priority));
            ticketCreate.setCategory(Category.fromValue(category));
            ticketCreate.setStatus(Status.NEW); // Initial status

            Ticket createdTicket = apiClient.createTicket(ticketCreate); // Call the API
            createTicketDialog.setVisible(false); // Hide the dialog
            loadTickets(); // Refresh the ticket list

            JOptionPane.showMessageDialog(this, "Ticket created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error creating ticket: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
