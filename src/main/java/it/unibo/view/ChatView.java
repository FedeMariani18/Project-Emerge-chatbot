package it.unibo.view;

import java.util.function.Consumer;

public interface ChatView {

    public void setVisible(Boolean visible);
    
    public void showAgentMessage(String message);

    public void showLoading(Boolean show);
    
    public void setEnabled(Boolean enabled);

    public void setOnMessageSent(Consumer<String> handler);
}
