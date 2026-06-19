package it.unibo.logic;

public interface Model {

    /**
     * Implement the interaction with the agent, by sending him a message and waiting a response
     * @param message the message sended to the agent
     * @return the response of the agent
     * @throws Exception 
     */
    String askAgent(String message) throws Exception;
    
    /**
     * Close the connection with of the sender
     */
    void closeConnection();
}
