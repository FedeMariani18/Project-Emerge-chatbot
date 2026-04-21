package it.unibo.logic;

import java.util.Map;
import java.util.Map.Entry;

public class Formation {
    String name;
    String label;
    String description;
    Map<String, String> parameters;

    /***
     * @param label the name of the formation
     * @param description the description of the formation
     * @param parameters an association of  
     */
    public Formation (String name, String label, String description, Map<String, String> parameters) {
        this.name = name;
        this.label = label;
        this.description = description;
        this.parameters = Map.copyOf(parameters);
    }

    public Formation() {}

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "Name: " + this.getName() +
            "\nLabel: " + this.getLabel() +
            "\nDescription: " + this.getDescription() + 
            "\nParameters: " + parametersToString();
    }

    public String parametersToString() {
        String res = "";
        for (Entry<String, String> param : this.getParameters().entrySet()) {
            res += "\n\tName: " + param.getKey() +
                "\n\tDescription: " + param.getValue();
        }
        return res;
    }
}
