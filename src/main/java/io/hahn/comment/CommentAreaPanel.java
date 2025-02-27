package io.hahn.comment;

import io.hahn.ApiClient;
import io.hahn.comment.dto.Comment;
import io.hahn.comment.dto.CommentPage;
import io.hahn.ticket.dto.Ticket;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static javax.swing.SwingUtilities.invokeLater;

public class CommentAreaPanel extends JPanel {

    private final ApiClient apiClient;
    private final Ticket currentTicket;
    private final DefaultListModel<Comment> commentListModel;

    public CommentAreaPanel(ApiClient apiClient, Ticket currentTicket) {
        this.apiClient = apiClient;
        this.currentTicket = currentTicket;
        setLayout(new MigLayout());

        if(apiClient.getAccount().hasRole("IT")) {
            add(new CommentFormPanel(apiClient, currentTicket, this::loadComments), "wrap, growx");
        }
        commentListModel = new DefaultListModel<>();
        JList<Comment> commentList = new JList<>(commentListModel);
        CommentView commentView = new CommentView(apiClient, currentTicket);
        commentList.setCellRenderer(commentView);
        commentList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    int index = commentList.locationToIndex(e.getPoint());
                    commentView.openCommentHistory(commentListModel.get(index));
                }
            }
        });
        JScrollPane commentListScrollPane = new JScrollPane(commentList);
        commentListScrollPane.setPreferredSize(new Dimension(
                520, 0
        ));
        add(commentListScrollPane, "wrap, growx, height 200::");

        loadComments();
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
}

