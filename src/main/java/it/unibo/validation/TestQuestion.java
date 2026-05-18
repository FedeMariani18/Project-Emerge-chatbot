package it.unibo.validation;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.List;
 
public class TestQuestion {
    
    @JsonProperty("id")
    private int id;
    
    @JsonProperty("domanda")
    private String domanda;
    
    @JsonProperty("expectedTool")
    private String expectedTool;
    
    @JsonProperty("descrizione")
    private String descrizione;
    
    public TestQuestion() {
    }
    
    public TestQuestion(int id, String domanda, String expectedTool, String descrizione) {
        this.id = id;
        this.domanda = domanda;
        this.expectedTool = expectedTool;
        this.descrizione = descrizione;
    }
    
    // Getter
    public int getId() {
        return id;
    }
    
    public String getDomanda() {
        return domanda;
    }
    
    public String getExpectedTool() {
        return expectedTool;
    }
    
    public String getDescrizione() {
        return descrizione;
    }
    
    /**
     * Return a list of Expected Tools for the Question
     * Es: "getAvailableFormations,validateFormation" → [getAvailableFormations, validateFormation]
     */
    public List<String> getExpectedToolList() {
        return Arrays.asList(expectedTool.split(","));
    }
    
    /**
     * Check if @param usedTool is an expected tool
     */
    public boolean isExpectedTool(String usedTool) {
        return getExpectedToolList().stream()
            .anyMatch(tool -> tool.trim().equalsIgnoreCase(usedTool));
    }
    
    /**
     * Return the number of expected tools
     */
    public int getNumToolAttesi() {
        return getExpectedToolList().size();
    }
    
    @Override
    public String toString() {
        return "TestQuestion{" +
                "id=" + id +
                ", domanda='" + domanda + '\'' +
                ", expectedTool='" + expectedTool + '\'' +
                ", descrizione='" + descrizione + '\'' +
                '}';
    }
}
