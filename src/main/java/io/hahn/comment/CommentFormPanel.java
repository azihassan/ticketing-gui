package io.hahn.comment;

import com.formdev.flatlaf.util.StringUtils;
import io.hahn.ApiClient;
import io.hahn.comment.dto.Comment;
import io.hahn.ticket.dto.Ticket;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

import static javax.swing.SwingUtilities.invokeLater;

class CommentFormPanel extends JPanel {
    private final JTextArea newCommentTextArea;
    private final JButton submitCommentButton;
    private final ApiClient apiClient;
    private final Ticket currentTicket;
    private final Runnable onSuccess;
    private final Comment prefilledComment;

    CommentFormPanel(ApiClient apiClient, Ticket currentTicket, Runnable onSuccess) {
        this(apiClient, currentTicket, onSuccess, new Comment().text(""));
    }

    CommentFormPanel(ApiClient apiClient, Ticket currentTicket, Runnable onSuccess, Comment prefilledComment) {
        this.apiClient = apiClient;
        this.currentTicket = currentTicket;
        this.onSuccess = onSuccess;
        this.prefilledComment = prefilledComment;
        setLayout(new MigLayout());
        newCommentTextArea = new JTextArea(3, 40);
        newCommentTextArea.setText(prefilledComment.getText());
        newCommentTextArea.setBorder(BorderFactory.createDashedBorder(Color.GRAY));
        JScrollPane newCommentScrollPane = new JScrollPane(newCommentTextArea);
        add(newCommentScrollPane, "wrap, growx");

        submitCommentButton = new JButton("Submit Comment");
        submitCommentButton.addActionListener(e -> submitComment());
        disableSubmissionWhenCommentIsEmpty();
        add(submitCommentButton, "wrap, align right");
    }

    private void disableSubmissionWhenCommentIsEmpty() {
        newCommentTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                submitCommentButton.setEnabled(!StringUtils.isTrimmedEmpty(newCommentTextArea.getText()));
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                submitCommentButton.setEnabled(!StringUtils.isTrimmedEmpty(newCommentTextArea.getText()));
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                submitCommentButton.setEnabled(!StringUtils.isTrimmedEmpty(newCommentTextArea.getText()));
            }
        });

    }

    private void submitComment() {
        String commentText = newCommentTextArea.getText();
        submitCommentButton.setEnabled(false);
        new Thread(() -> {
            try {
                prefilledComment.setText(commentText);
                apiClient.publishComment(currentTicket.getId(), prefilledComment);

                invokeLater(() -> {
                    newCommentTextArea.setText("");
                    JOptionPane.showMessageDialog(this, "Comment submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                });
                onSuccess.run();
            } catch (Exception ex) {
                invokeLater(() -> JOptionPane.showMessageDialog(this, "Error submitting comment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
                ex.printStackTrace();
            } finally {
                submitCommentButton.setEnabled(true);
            }
        }).start();
    }
}
