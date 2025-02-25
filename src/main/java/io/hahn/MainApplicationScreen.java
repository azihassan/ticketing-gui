package io.hahn;

import javax.swing.*;
import java.awt.*;

public class MainApplicationScreen extends JFrame {
    private ApiClient apiClient;
    private TicketListPanel ticketListPanel; // Add this

    public MainApplicationScreen(ApiClient apiClient) {
        this.apiClient = apiClient;
        // ... other UI setup ...

        ticketListPanel = new TicketListPanel(apiClient); // Initialize
        add(ticketListPanel, BorderLayout.CENTER); // Add to the center of your frame
        pack();
        setSize(800, 600);

        // ... other UI elements and event listeners ...
    }

    // ... other methods ...

    // If you have a refresh button, you can call this:
    private void refreshTicketList() {
        ticketListPanel.loadTickets();
    }
}
