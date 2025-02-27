package io.hahn;

import io.hahn.ticket.TicketListPanel;

import javax.swing.*;
import java.awt.*;

public class MainApplicationScreen extends JFrame {
    public MainApplicationScreen(ApiClient apiClient) {
        super("Ticket List");
        TicketListPanel ticketListPanel = new TicketListPanel(apiClient);
        add(ticketListPanel, BorderLayout.CENTER);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
