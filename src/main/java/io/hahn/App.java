package io.hahn;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class App extends JFrame {

    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public App(ApiClient apiClient) {
        super("Login");
        // Your API client

        // Set up the layout (using GridBagLayout for flexibility)
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add some padding

        // Username label and field
        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(usernameLabel, gbc);

        usernameField = new JTextField(20); // 20 columns wide
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(usernameField, gbc);

        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(passwordField, gbc);

        // Login button
        JButton loginButton = new JButton("Login");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST; // Align to the right
        add(loginButton, gbc);

        // Login button action listener
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword()); // Get password

                try {
                    apiClient.login(username, password);
                    // Login successful, open the main application window
                    SwingUtilities.invokeLater(() -> {
                        new MainApplicationScreen(apiClient).setVisible(true); // Pass the apiClient
                        dispose(); // Close the login window
                    });


                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(App.this, "Login failed: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace(); // Print the error for debugging
                }
            }
        });

        pack(); // Adjusts the window size to fit the components
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
    }


    public static void main(String[] args) {
        ApiClient apiClient = new ApiClient(); // Initialize your API client
        SwingUtilities.invokeLater(() -> new App(apiClient).setVisible(true));
    }
}
