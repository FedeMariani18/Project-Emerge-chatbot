package it.unibo.logic;

import java.util.Map;

public class Formation {
    String name;
    String description;
    Map<String, Object> attributes;

    public Formation (String name, String description, Map<String, Object> attributes) {
        this.name = name;
        this.description = description;
        this.attributes = Map.copyOf(attributes);
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
