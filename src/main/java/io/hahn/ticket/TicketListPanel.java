package io.hahn.ticket;

import io.hahn.ApiClient;
import io.hahn.ticket.dto.Ticket;
import io.hahn.ticket.dto.TicketPage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TicketListPanel extends JPanel {

    private final JTable ticketTable;
    private final DefaultTableModel tableModel;
    private final TicketCreationDialog createTicketDialog;
    private final ApiClient apiClient;


    public TicketListPanel(ApiClient apiClient) {
        this.apiClient = apiClient;
        setLayout(new MigLayout());

        JPanel searchPanel = new SearchPanel(this::loadTickets);
        add(searchPanel, "grow, wrap");

        tableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Priority", "Status", "Created At"}, 0);
        ticketTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        //ticketTable.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        //ticketTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Title

        JScrollPane scrollPane = new JScrollPane(ticketTable);
        add(scrollPane, "grow, wrap");

        loadTickets();

        JButton createTicketButton = new JButton("Create New Ticket");
        createTicketButton.addActionListener(e -> showCreateTicketDialog());
        add(createTicketButton, "wrap");

        createTicketDialog = new TicketCreationDialog(apiClient, this::loadTickets, (JFrame) SwingUtilities.getWindowAncestor(this), "Create Ticket", true);

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
    }

    public void loadTickets() {
        loadTickets(null, null);
    }

    public void loadTickets(Long id, String status) {
        new Thread(() -> {
            try {
                TicketPage ticketPage = apiClient.getTickets(status, id); // Get all tickets (no filters initially)
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
        createTicketDialog.resetFields();
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
}