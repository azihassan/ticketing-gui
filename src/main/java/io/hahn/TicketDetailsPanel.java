package io.hahn;

import io.hahn.dto.Status;
import io.hahn.dto.Ticket;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

class TicketDetailsPanel extends JPanel {
    private final ApiClient apiClient;
    private final Ticket currentTicket;
    private final JTextArea ticketDetailsArea;
    private final JComboBox<String> statusComboBox;


    public TicketDetailsPanel(ApiClient apiClient, Ticket ticket) {
        this.apiClient = apiClient;
        this.currentTicket = ticket;
        setLayout(new MigLayout());

        ticketDetailsArea = new JTextArea(10, 40);
        ticketDetailsArea.setEditable(false);
        JScrollPane ticketDetailsScrollPane = new JScrollPane(ticketDetailsArea);
        ticketDetailsScrollPane.setBorder(BorderFactory.createTitledBorder("Title: " + ticket.getTitle()));
        add(ticketDetailsScrollPane, "wrap");

        JPanel statusPanel = new JPanel(new MigLayout());
        JLabel statusLabel = new JLabel("Status:");
        statusComboBox = new JComboBox<>(new String[]{"NEW", "IN_PROGRESS", "RESOLVED"});
        JButton updateStatusButton = new JButton("Update Status");
        JButton viewStatusHistoryButton = new JButton("View History");
        viewStatusHistoryButton.setEnabled(false);
        viewStatusHistoryButton.setToolTipText("Not implemented yet");
        statusPanel.add(statusLabel);
        statusPanel.add(statusComboBox);
        statusPanel.add(updateStatusButton);
        statusPanel.add(viewStatusHistoryButton, "growx, wrap");
        add(statusPanel, "growx, wrap");
        updateStatusButton.addActionListener(e -> updateTicketStatus());

        loadTicketDetails();
        add(new CommentAreaPanel(apiClient, currentTicket), "growx");
    }

    private void loadTicketDetails() {
        ticketDetailsArea.setText(
            "Priority: " + currentTicket.getPriority() + "\n" +
            "Category: " + currentTicket.getCategory() + "\n" +
            "Status: " + currentTicket.getStatus() + "\n" +
            "Created At: " + currentTicket.getCreatedAt() + "\n" +
            "Created By: " + currentTicket.getCreatedBy().getUsername() + "\n\n" +
            currentTicket.getDescription()
        );
        statusComboBox.setSelectedItem(currentTicket.getStatus().getValue());
    }


    private void updateTicketStatus() {
        String newStatus = (String) statusComboBox.getSelectedItem();
        try {
            apiClient.updateTicketStatus(currentTicket.getId(), newStatus);
            currentTicket.setStatus(Status.fromValue(newStatus)); // Update the current ticket object
            loadTicketDetails();
            JOptionPane.showMessageDialog(this, "Status updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating status: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

}