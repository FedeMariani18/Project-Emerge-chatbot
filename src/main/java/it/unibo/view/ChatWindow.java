package it.unibo.view;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;

import static it.unibo.common.Constants.*;

public class ChatWindow extends JFrame {

    public ChatWindow(ChatPanel chatPanel) {
        setTitle("Project Emerge Chatbot");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension((int)FRAME_WIDTH/2, (int)FRAME_HEIGHT/2));

        add(chatPanel, BorderLayout.CENTER);
    }
}