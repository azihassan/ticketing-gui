package io.hahn.comment;

import io.hahn.ApiClient;
import io.hahn.comment.dto.Comment;
import io.hahn.ticket.dto.Ticket;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

class CommentView extends JPanel implements ListCellRenderer<Comment> {
    private final ApiClient apiClient;
    private final Ticket currentTicket;
    private final JLabel commentLabel;

    public CommentView(ApiClient apiClient, Ticket currentTicket) {
        this.apiClient = apiClient;
        this.currentTicket = currentTicket;
        this.commentLabel = new JLabel();
        setLayout(new MigLayout());
        add(commentLabel, "wrap");
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Comment> list, Comment value, int index, boolean isSelected, boolean cellHasFocus) {
        commentLabel.setText(value.getCreatedBy().getUsername() + ": " + value.getText());
        return this;
    }

    public void openCommentHistory(Comment comment) {
        var commentHistoryFrame = new JFrame("Comment history - " + comment.getId());
        var commentHistoryPanel = new CommentHistoryPanel(apiClient, currentTicket, comment);
        commentHistoryFrame.setContentPane(commentHistoryPanel);
        commentHistoryFrame.pack();
        commentHistoryFrame.setLocationRelativeTo(null);
        commentHistoryFrame.setVisible(true);
    }
}
