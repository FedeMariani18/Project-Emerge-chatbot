package it.unibo.controller;

import it.unibo.logic.Model;
import it.unibo.view.ChatView;


public class Controller {
    ChatView view;
    Model model;
    
    public Controller(ChatView view, Model model) {
        this.view = view;
        this.model = model;

        // To set the action listener of the send button
        this.view.setOnMessageSent(userInput -> processUserInput(userInput));
    }

    public void processUserInput(String userMessage) {
        view.setEnabled(false);
        view.showLoading(true);

        new Thread(() -> {
            try {
                String response = model.askAgent(userMessage);
                
                // display the response in the GUI
                view.showAgentMessage(response);
            } catch (Exception e) {
                view.showAgentMessage("Error: " + e.getMessage());
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
