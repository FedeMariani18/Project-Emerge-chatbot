package it.unibo.view;

import javax.swing.*;

import static it.unibo.common.Constants.*;

public class ChatWindow extends JFrame {
    private ChatPanel chatPanel;

    public ChatWindow() {
        setTitle("Project Emerge Chatbot");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new java.awt.Dimension((int)FRAME_WIDTH/2, (int)FRAME_HEIGHT/2));

        chatPanel = new ChatPanel();
        add(chatPanel, java.awt.BorderLayout.CENTER);
    }
}