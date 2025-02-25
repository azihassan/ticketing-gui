package io.hahn;

import com.formdev.flatlaf.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;

class SearchPanel extends JPanel {
    public SearchPanel(BiConsumer<Long, String> onSubmit) {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        var searchIdLabel = new JLabel("Search by ID:");
        var searchIdField = new JTextField(10);
        add(searchIdLabel);
        add(searchIdField);

        var searchStatusLabel = new JLabel("Search by Status:");
        var searchStatusComboBox = new JComboBox<>(new String[]{"", "NEW", "IN_PROGRESS", "RESOLVED"}); // Add empty option
        add(searchStatusLabel);
        add(searchStatusComboBox);

        var searchButton = new JButton("Search");
        searchButton.addActionListener(e -> {
            try {
                onSubmit.accept(
                        StringUtils.isEmpty(searchIdField.getText()) ? null : Long.parseLong(searchIdField.getText()),
                        (String) searchStatusComboBox.getSelectedItem()
                );
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(searchButton);
    }
}
