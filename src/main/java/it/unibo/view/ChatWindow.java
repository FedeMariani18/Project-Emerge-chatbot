package it.unibo.view;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.function.Consumer;

import static it.unibo.common.Constants.*;

public class ChatWindow extends JFrame implements ChatView{

    ChatPanel chatPanel;

    public ChatWindow() {
        setTitle("Project Emerge Chatbot");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension((int)FRAME_WIDTH/2, (int)FRAME_HEIGHT/2));

        chatPanel = new ChatPanel(); 
        add(chatPanel, BorderLayout.CENTER);
    }

    private ChatPanel getChatPanel(){
        return chatPanel;
    }

    @Override
    public void setVisible(Boolean visible) {
        SwingUtilities.invokeLater(() -> {
            this.setVisible(true);
        });
    }

    @Override
    public void showAgentMessage(String response) {
        SwingUtilities.invokeLater(() -> {
            getChatPanel().displayAgentMessage(response);
        });
    }

    @Override
    public void showLoading(Boolean show) {
        SwingUtilities.invokeLater(() -> {
            getChatPanel().showLoading(show);
        });    
    }

    @Override
    public void setEnabled(Boolean enabled) {
        SwingUtilities.invokeLater(() -> {
            getChatPanel().setEnabled(true);
        });
    }

    @Override
    public void setOnMessageSent(Consumer<String> handler) {
        getChatPanel().setOnMessageSent(handler);
    }

    
}