package it.unibo.logic;

import java.util.List;

public class Formation {
    String name;
    String description;
    List<Parameter<Double>> parameters;

    /***
     * @param name the name of the formation
     * @param description the description of the formation
     * @param parameters an association of  
     */
    public Formation (String name, String description, List<Parameter<Double>> parameters) {
        this.name = name;
        this.description = description;
        this.parameters = List.copyOf(parameters);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Parameter<Double>> getParameters() {
        return parameters;
    }
}
