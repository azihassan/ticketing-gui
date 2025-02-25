package io.hahn;

import io.hahn.dto.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;


class TicketDetailsPanel extends JPanel {
    private final ApiClient apiClient;
    private final Ticket currentTicket;
    private final JTextArea ticketDetailsArea;
    private final JList<Comment> commentList;
    private final DefaultListModel<Comment> commentListModel;
    private final JComboBox<String> statusComboBox;
    private final JButton updateStatusButton;

    private final JTextArea newCommentTextArea;
    private final JButton submitCommentButton;


    public TicketDetailsPanel(ApiClient apiClient, Ticket ticket) {
        this.apiClient = apiClient;
        this.currentTicket = ticket;
        setLayout(new BorderLayout());

        // Ticket Details Area
        ticketDetailsArea = new JTextArea(10, 40);
        ticketDetailsArea.setEditable(false);
        JScrollPane ticketDetailsScrollPane = new JScrollPane(ticketDetailsArea);
        add(ticketDetailsScrollPane, BorderLayout.NORTH);

        // Comments List
        commentListModel = new DefaultListModel<>();
        commentList = new JList<>(commentListModel);
        JScrollPane commentListScrollPane = new JScrollPane(commentList);
        add(commentListScrollPane, BorderLayout.CENTER);

        // Status Update
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Status:");
        statusComboBox = new JComboBox<>(new String[]{"NEW", "IN_PROGRESS", "RESOLVED"});
        updateStatusButton = new JButton("Update Status");

        statusPanel.add(statusLabel);
        statusPanel.add(statusComboBox);
        statusPanel.add(updateStatusButton);
        add(statusPanel, BorderLayout.SOUTH);

        // Combined Panel for New Comment and Status Update
        JPanel combinedPanel = new JPanel(new BorderLayout());

        // New Comment Area (add to the NORTH of the combined panel)
        JPanel newCommentPanel = new JPanel(new BorderLayout());
        newCommentTextArea = new JTextArea(3, 40); // 3 rows, 40 columns
        JScrollPane newCommentScrollPane = new JScrollPane(newCommentTextArea);
        newCommentPanel.add(newCommentScrollPane, BorderLayout.CENTER);

        submitCommentButton = new JButton("Submit Comment");
        submitCommentButton.addActionListener(e -> submitComment());
        newCommentPanel.add(submitCommentButton, BorderLayout.SOUTH);
        combinedPanel.add(statusPanel, BorderLayout.NORTH); // Add to combined pane

        combinedPanel.add(statusPanel, BorderLayout.SOUTH); // Add to combined pane
        add(combinedPanel, BorderLayout.SOUTH); // Add below the status update


        loadTicketDetails();
        loadComments();

        updateStatusButton.addActionListener(e -> updateTicketStatus());
    }

    private void loadTicketDetails() {
        ticketDetailsArea.setText(
                "ID: " + currentTicket.getId() + "\n" +
                        "Title: " + currentTicket.getTitle() + "\n" +
                        "Description: " + currentTicket.getDescription() + "\n" +
                        "Priority: " + currentTicket.getPriority() + "\n" +
                        "Category: " + currentTicket.getCategory() + "\n" +
                        "Status: " + currentTicket.getStatus() + "\n" +
                        "Created At: " + currentTicket.getCreatedAt() + "\n" +
                        "Created By: " + currentTicket.getCreatedBy().getUsername() + "\n"
        );
        statusComboBox.setSelectedItem(currentTicket.getStatus()); // Set current status
    }

    private void loadComments() {
        new Thread(() -> {
            try {
                CommentPage commentPage = apiClient.getCommentsByTicketId(currentTicket.getId());
                List<Comment> comments = commentPage.getContent();

                SwingUtilities.invokeLater(() -> {
                    commentListModel.removeAllElements();
                    if (comments != null) {
                        for (Comment comment : comments) {
                            commentListModel.addElement(comment);
                        }
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error loading comments: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                });
            }
        }).start();
    }

    private void updateTicketStatus() {
        String newStatus = (String) statusComboBox.getSelectedItem();
        try {
            apiClient.updateTicketStatus(currentTicket.getId(), newStatus);
            currentTicket.setStatus(Status.fromValue(newStatus)); // Update the current ticket object
            loadTicketDetails(); // Refresh the displayed ticket details
            JOptionPane.showMessageDialog(this, "Status updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating status: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void submitComment() {
        String commentText = newCommentTextArea.getText();
        if (commentText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a comment.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            CommentCreate commentCreate = new CommentCreate();
            commentCreate.setText(commentText);

            Comment createdComment = apiClient.addComment(currentTicket.getId(), commentCreate); // Call the API

            newCommentTextArea.setText(""); // Clear the text area
            loadComments(); // Refresh the comment list

            JOptionPane.showMessageDialog(this, "Comment submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error submitting comment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}