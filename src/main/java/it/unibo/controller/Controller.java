package it.unibo.controller;

import javax.swing.SwingUtilities;

import it.unibo.logic.Agent;
import it.unibo.logic.FormationProvider;
import it.unibo.logic.FormationProviderImpl;
import it.unibo.logic.Sender;
import it.unibo.logic.MqttSender;
import it.unibo.logic.ToolsHandler;
import it.unibo.view.ChatPanel;
import it.unibo.view.ChatWindow;

public class Controller {
    ChatWindow chatWindow;
    ChatPanel chatPanel;
    Agent agent;
    
    public Controller() {
        chatWindow = new ChatWindow();
        chatPanel = chatWindow.getChatPanel();

        FormationProvider formationProvider = new FormationProviderImpl();
        Sender sender = new MqttSender();
        if (!sender.connect()) System.out.println("Unable to connect to the MQTT broker");
        ToolsHandler tools = new ToolsHandler(formationProvider, sender);

        agent = new Agent(tools);
        
        // To set the action listener of the send button
        chatPanel.setOnMessageSent(userInput -> processUserInput(userInput));
    }

    public void processUserInput(String userMessage) {
        chatPanel.setEnabled(false);
        chatPanel.showLoading(true);

        new Thread(() -> {
            try {
                String response = agent.chat(userMessage);
                
                // display the response in the GUI
                SwingUtilities.invokeLater(() -> {
                    chatPanel.displayAgentMessage(response);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    chatPanel.displayAgentMessage("Errore: " + e.getMessage());
                });
            } finally {
                chatPanel.showLoading(false);
                chatPanel.setEnabled(true);
            }
        }).start();
    }

    public void start(){
        SwingUtilities.invokeLater(() -> {
            chatWindow.setVisible(true);
        });
    }
}
