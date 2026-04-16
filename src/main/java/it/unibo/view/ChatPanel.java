package it.unibo.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static it.unibo.common.Constants.*;

public class ChatPanel extends JPanel {
    private static final int PADDING = 10;
    private static final int SPACING = 5;
    
    private MessagePanel messagePanel;
    private JTextField inputField;
    private JButton sendButton;
    
    public ChatPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));

        // Panel for messages with scroll
        messagePanel = new MessagePanel();
        JScrollPane scrollPane = new JScrollPane(messagePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        // Bottom panel with input and button
        JPanel bottomPanel = new JPanel(new BorderLayout(SPACING, 0));
        bottomPanel.setBackground(BACKGROUND_COLOR);

        inputField = new JTextField("dgggg gggggggggg gggg gggg ggggggg ggggggggg ggg ggggggggg ggggg ggggggg ggggg ggggg gg gg ggggg gg ggg ggggggg ggg df dfg df gdf gdfgd fg dfg df g dgggg");
        inputField.setFont(DEFAULT_FONT);
        inputField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        inputField.setPreferredSize(new Dimension(0, INPUT_BUTTON_HEIGHT));
        inputField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });

        sendButton = new JButton("Invia");
        sendButton.setPreferredSize(new Dimension(BUTTON_WIDTH, INPUT_BUTTON_HEIGHT));
        sendButton.setBackground(PRIMARY_COLOR);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setFont(BUTTON_FONT);
        sendButton.addActionListener(e -> sendMessage());

        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        // Add components to the main panel
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void displayUserMessage(String message) {
        messagePanel.addMessage(message, true);
    }

    public void displayAgentMessage(String message) {
        messagePanel.addMessage(message, false);
    }

    public void displaySystemMessage(String message) {
        messagePanel.addMessage("SYS: " + message, false);
    }

    private void sendMessage() {
        String userMessage = inputField.getText().trim();
        if (!userMessage.isEmpty()) {
            displayUserMessage(userMessage);
            displayAgentMessage("ok of course");
            inputField.setText("");
            inputField.requestFocus();
        }
    }

    public void setEnabled(boolean enabled) {
        inputField.setEnabled(enabled);
        sendButton.setEnabled(enabled);
    }
}