package io.hahn;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

class LoginPanel extends JPanel {

    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public LoginPanel(ApiClient apiClient) {
        setLayout(new MigLayout());
        JLabel usernameLabel = new JLabel("Username:");
        add(usernameLabel, "grow, align left, wrap");

        usernameField = new JTextField(20);
        add(usernameField, "grow, wrap");

        JLabel passwordLabel = new JLabel("Password:");
        add(passwordLabel, "grow, align left, wrap");

        passwordField = new JPasswordField(20);
        add(passwordField, "grow, wrap");

        JButton loginButton = new JButton("Login");
        add(loginButton, "grow, wrap");

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            SwingUtilities.invokeLater(() -> loginButton.setEnabled(false));

            new Thread(() -> {
                try {
                    apiClient.login(username, password);
                    SwingUtilities.invokeLater(() -> {
                        setVisible(false);
                        new MainApplicationScreen(apiClient).setVisible(true);
                        SwingUtilities.getWindowAncestor(this).dispose();
                    });

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Login failed: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } finally {
                    SwingUtilities.invokeLater(() -> loginButton.setEnabled(true));
                }
            }).start();
        });
    }
}
