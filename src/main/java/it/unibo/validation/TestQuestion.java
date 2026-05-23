package it.unibo.validation;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
 
public class TestQuestion {
    
    @JsonProperty("id")
    private int id;
    
    @JsonProperty("question")
    private String question;
    
    @JsonProperty("expectedTool")
    private String expectedTool;
    
    @JsonProperty("description")
    private String description;
    
    public TestQuestion() {
    }
    
    public TestQuestion(int id, String question, String expectedTool, String description) {
        this.id = id;
        this.question = question;
        this.expectedTool = expectedTool;
        this.description = description;
    }
    
    // Getter
    public int getId() {
        return id;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public String getExpectedTool() {
        return expectedTool;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Return a set of Expected Tools for the Question
     * Es: "getAvailableFormations,validateFormation" → [getAvailableFormations, validateFormation]
     */
    public Set<String> getExpectedToolList() {
        return new HashSet<String>(List.of(expectedTool.split(",")));
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
                ", question='" + question + '\'' +
                ", expectedTool='" + expectedTool + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
