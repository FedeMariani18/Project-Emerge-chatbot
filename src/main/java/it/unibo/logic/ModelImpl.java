package it.unibo.logic;

public class ModelImpl implements Model {

    private final Agent agent;
    private final Sender sender;

    public ModelImpl() {

        FormationProvider formationProvider = new FormationProviderImpl();

        sender = new MqttSender();

        if (!sender.connect()) {throw new IllegalStateException("Unable to connect to MQTT broker");}

        ToolsHandler tools = new ToolsHandler(formationProvider, sender);

        agent = new Agent(tools);
    }

    @Override
    public String askAgent(String message) throws Exception {
        return agent.chat(message);
    }

    @Override
    public void closeConnection() {
        sender.disconnect();
    }
}
