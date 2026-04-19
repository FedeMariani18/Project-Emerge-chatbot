package it.unibo.logic;

public interface Sender {

    /**
     * Sends a formation command to the drones
     * @param formation the name of the formation (e.g. "V", "rectangle")
     * @param parameters additional parameters (JSON or string)
     * @return true if successful
     */
    boolean sendFormation(String formation, String parameters);
    
    /**
     * Connects the MQTT client to the broker
     * @return true if the connection succeeded
     */
    boolean connect();
    
    /**
     * Disconnects the MQTT client from the broker
     * @return false if something gone wrong
     */
    boolean disconnect();
    
    /**
     * Checks whether the client is connected
     * @return true if connected
     */
    boolean isConnected();
}
