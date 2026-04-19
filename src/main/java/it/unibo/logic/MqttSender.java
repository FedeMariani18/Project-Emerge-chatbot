package it.unibo.logic;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MqttSender implements Sender{

    private static final String BROKER = "tcp://broker.emqx.io:1883"; //System.getenv().getOrDefault("MQTT_URL", "tcp://localhost:1883")
    private static final String CLIENT_ID = "ChatbotClient";
    private static final String FORMATION_TOPIC = "sensing";

    private MqttClient client;

    public MqttSender() {}

    @Override
    public boolean sendFormation(String formation, String parameters) {
        try {
            client.publish(FORMATION_TOPIC, (formation + "\n" + parameters).getBytes(), 1, false);
            return true;
        } catch (MqttException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean connect() {
        try{
            client = new MqttClient(BROKER, CLIENT_ID);
            client.connect();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean disconnect() {
        try{
            if(client.isConnected()) {
                client.disconnect();
                client.close();    
                return true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean isConnected() {
        return client != null && client.isConnected();
    }
    
}
