package io.hahn.comment;

import io.hahn.ApiClient;
import io.hahn.comment.dto.Comment;
import io.hahn.ticket.dto.Ticket;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static javax.swing.SwingUtilities.invokeLater;

class CommentHistoryPanel extends JPanel {
    private final ApiClient apiClient;
    private final Ticket currentTicket;
    private final Comment comment;

    public CommentHistoryPanel(ApiClient apiClient, Ticket currentTicket, Comment comment) {
        this.apiClient = apiClient;
        this.currentTicket = currentTicket;
        this.comment = comment;
        setLayout(new MigLayout());
        //setPreferredSize(new Dimension(800, 600));

        if(apiClient.getAccount().getId().equals(comment.getCreatedBy().getId())) {
            setBorder(BorderFactory.createTitledBorder("Update comment " + comment.getId()));
            add(new CommentFormPanel(apiClient, currentTicket, () -> SwingUtilities.getWindowAncestor(this).dispose(), comment), "wrap");
        } else {
            setBorder(BorderFactory.createTitledBorder("Viewing comment " + comment.getId()));
        }

        add(new JLabel("Comment history"), "wrap");
        var commentHistoryPanel = new JPanel(new MigLayout());
        var scrollPane = new JScrollPane(commentHistoryPanel);
        scrollPane.setPreferredSize(new Dimension(520, 300));
        add(scrollPane, "growx");

        new Thread(() -> {
            try {
                List<Comment> commentHistory = apiClient.getCommentHistory(comment.getTicketId(), comment.getId());
                System.out.println("Loaded " + commentHistory.size() + " comments in edit history");
                invokeLater(() -> {
                    for (Comment oldComment : commentHistory) {
                        this.addComment(oldComment, commentHistoryPanel);
                    }
                    revalidate();
                    repaint();
                    System.out.println("Comment history scheduled for display");
                });
            } catch (Exception e) {
                invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error loading comment history: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                });
            }
        }).start();
    }

    void addComment(Comment oldComment, JPanel commentHistoryPanel) {
        commentHistoryPanel.add(new JLabel(oldComment.getCreatedAt().toString()), "wrap");
        //comment.account_id isn't stored in the audit
        //displaying the username of the most recent comment instead
        //because a comment can only be modified by its owner
        commentHistoryPanel.add(new JLabel(comment.getCreatedBy().getUsername() + " says"), "wrap");
        commentHistoryPanel.add(new JLabel(oldComment.getText()), "wrap, gapbottom 30");
    }
}
