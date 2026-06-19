package it.unibo.controller;

import it.unibo.logic.Agent;
import it.unibo.logic.FormationProvider;
import it.unibo.logic.FormationProviderImpl;
import it.unibo.logic.Sender;
import it.unibo.logic.MqttSender;
import it.unibo.logic.ToolsHandler;
import it.unibo.view.ChatView;
import it.unibo.view.ChatWindow;


public class Controller {
    ChatView view;
    Agent agent;
    
    public Controller() {
        view = new ChatWindow();

        FormationProvider formationProvider = new FormationProviderImpl();
        Sender sender = new MqttSender();
        if (!sender.connect()) System.out.println("Unable to connect to the MQTT broker");
        ToolsHandler tools = new ToolsHandler(formationProvider, sender);

        agent = new Agent(tools);
        
        // To set the action listener of the send button
        view.setOnMessageSent(userInput -> processUserInput(userInput));
    }

    public void processUserInput(String userMessage) {
        view.setEnabled(false);
        view.showLoading(true);

        new Thread(() -> {
            try {
                String response = agent.chat(userMessage);
                
                // display the response in the GUI
                view.showAgentMessage(response);
            } catch (Exception e) {
                view.showAgentMessage("Errore: " + e.getMessage());
            } finally {
                view.showLoading(false);
                view.setEnabled(true);
            }
        }).start();
    }

    public void start(){
        view.setVisible(true);
    }
}
