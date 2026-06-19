package it.unibo.view;

import java.util.function.Consumer;

public interface ChatView {

    /**
     * Sets the visibility of the chat window.
     * 
     * @param visible {@code true} to show the window, {@code false} to hide it
     */
    public void setVisible(Boolean visible);
    
    /**
     * Displays an agent message in the chat area.
     * The message will be formatted and shown as an agent response within the message panel.
     * 
     * @param message the text of the agent's message to display
     */
    public void showAgentMessage(String message);

    /**
     * Controls the visibility of the loading indicator.
     * The loading indicator is typically shown while the agent is processing a user's message
     * and hidden when the response is ready.
     * 
     * @param show {@code true} to display the loading indicator, {@code false} to hide it
     */
    public void showLoading(Boolean show);
    
    /**
     * Enables or disables user interaction with the chat interface.
     * When disabled, the user cannot input messages or send them until the chat is re-enabled.
     * This is typically used to prevent user input while the agent is processing.
     * 
     * @param enabled {@code true} to enable user input, {@code false} to disable it
     */
    public void setEnabled(Boolean enabled);

    /**
     * Sets the handler to be invoked when the user sends a message.
     * The handler receives the text of the message that the user sent.
     * This is the connection point between the view and the application logic.
     * 
     * @param handler a {@code Consumer<String>} that will be invoked with the user's message
     *                when the send button is pressed or Enter is typed
     */
    public void setOnMessageSent(Consumer<String> handler);
}
