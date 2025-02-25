package io.hahn;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class App extends JFrame {

    public App(ApiClient apiClient) {
        super("Login");
        //.setVisible(true);
        setContentPane(new LoginPanel(apiClient));
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }


    public static void main(String[] args) {
        FlatLightLaf.setup();
        ApiClient apiClient = new ApiClient();
        SwingUtilities.invokeLater(() -> new App(apiClient).setVisible(true));
    }
}
