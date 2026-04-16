package it.unibo.controller;

import javax.swing.SwingUtilities;

import it.unibo.logic.Agent;
import it.unibo.view.ChatPanel;
import it.unibo.view.ChatWindow;

public class Controller {
    ChatWindow chatWindow;
    Agent agent;
    
    public Controller() {
        ChatPanel chatPanel = new ChatPanel(); 
        chatWindow = new ChatWindow(chatPanel);
        agent = new Agent();

        // to set the action listener of the send button
        chatPanel.setOnMessageSent(userInput -> {
            new Thread(() -> {
                try {
                    String response = agent.chat(userInput);
                    
                    // display the response in the GUI
                    SwingUtilities.invokeLater(() -> {
                        chatPanel.displayAgentMessage(response);
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        chatPanel.displayAgentMessage("Errore: " + e.getMessage());
                    });
                }
            }).start();
        });
    }

    public void start(){
        SwingUtilities.invokeLater(() -> {
            chatWindow.setVisible(true);
        });
    }
}
