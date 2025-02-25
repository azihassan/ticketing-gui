package io.hahn;

import com.formdev.flatlaf.util.StringUtils;
import io.hahn.dto.Comment;
import io.hahn.dto.CommentCreate;
import io.hahn.dto.CommentPage;
import io.hahn.dto.Ticket;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;

import static javax.swing.SwingUtilities.invokeLater;

class CommentAreaPanel extends JPanel {

    private final ApiClient apiClient;
    private final Ticket currentTicket;
    private final JTextArea newCommentTextArea;
    private final JButton submitCommentButton;
    private final DefaultListModel<Comment> commentListModel;

    public CommentAreaPanel(ApiClient apiClient, Ticket currentTicket) {
        this.apiClient = apiClient;
        this.currentTicket = currentTicket;
        setLayout(new MigLayout());

        JPanel newCommentPanel = new JPanel(new BorderLayout());
        newCommentTextArea = new JTextArea(2, 40);
        JScrollPane newCommentScrollPane = new JScrollPane(newCommentTextArea);
        newCommentPanel.add(newCommentScrollPane, BorderLayout.CENTER);
        add(newCommentScrollPane, "wrap, growx");

        submitCommentButton = new JButton("Submit Comment");
        submitCommentButton.addActionListener(e -> submitComment());
        disableSubmissionWhenCommentIsEmpty();
        add(submitCommentButton, "wrap, growx");

        commentListModel = new DefaultListModel<>();
        JList<Comment> commentList = new JList<>(commentListModel);
        commentList.setCellRenderer(new CommentView());
        JScrollPane commentListScrollPane = new JScrollPane(commentList);
        commentListScrollPane.setPreferredSize(new Dimension(
                commentListScrollPane.getWidth(), 200
        ));
        add(commentListScrollPane, "wrap, growx");

        loadComments();
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

    private void loadComments() {
        new Thread(() -> {
            try {
                CommentPage commentPage = apiClient.getCommentsByTicketId(currentTicket.getId());
                List<Comment> comments = commentPage.getContent();

                invokeLater(() -> {
                    commentListModel.removeAllElements();
                    if (comments != null) {
                        for (Comment comment : comments) {
                            commentListModel.addElement(comment);
                        }
                    }
                });
            } catch (Exception e) {
                invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error loading comments: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                });
            }
        }).start();
    }

    private void submitComment() {
        String commentText = newCommentTextArea.getText();
        submitCommentButton.setEnabled(false);
        new Thread(() -> {
            try {
                CommentCreate commentCreate = new CommentCreate();
                commentCreate.setText(commentText);
                apiClient.addComment(currentTicket.getId(), commentCreate);

                invokeLater(() -> {
                    newCommentTextArea.setText("");
                    JOptionPane.showMessageDialog(this, "Comment submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                });
                loadComments();
            } catch (Exception ex) {
                invokeLater(() -> JOptionPane.showMessageDialog(this, "Error submitting comment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
                ex.printStackTrace();
            } finally {
                submitCommentButton.setEnabled(true);
            }
        }).start();
    }
}

class CommentView extends JLabel implements ListCellRenderer<Comment> {
    @Override
    public Component getListCellRendererComponent(JList<? extends Comment> list, Comment value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(value.getCreatedBy().getUsername() + ": " + value.getText());
        return this;
    }
}
/*
class CommentView extends JPanel implements ListCellRenderer<Comment> {
    public CommentView() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Comment> list, Comment value, int index, boolean isSelected, boolean cellHasFocus) {
        add(new JLabel("On " + value.getCreatedAt() + ")"));
        add(new JLabel(value.getCreatedBy().getUsername() + " says:"));
        add(new JLabel(value.getText()));
        add(Box.createRigidArea(new Dimension(0, 10)));
        return this;
    }
}*/