package it.unibo;

import javax.swing.SwingUtilities;

import it.unibo.view.ChatWindow;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChatWindow window = new ChatWindow();
            window.setVisible(true);
        });
    }
}
