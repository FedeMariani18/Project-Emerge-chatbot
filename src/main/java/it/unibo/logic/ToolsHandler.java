package it.unibo.logic;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.langchain4j.agent.tool.Tool;

public class ToolsHandler {
    private FormationProvider formationProvider;
    private Sender sender;
    private ObjectMapper objMapper = new ObjectMapper();

    public ToolsHandler(FormationProvider formationProvider, Sender sender) {
        this.formationProvider = formationProvider;
        this.sender = sender;
    }

    @Tool("Ottieni una stringa json tutte le formazioni disponibili per i droni")
    public String getAvailableFormations(){
        return formationProvider.getFormationsJson();
    }

    @Tool("Valida la formazione che hai creato prima di inviarla ai droni")
    public String validateFormation(String formation) {
        System.out.println("validazione formazione:\n " + formation);
        
        try {
            JsonNode json = objMapper.readTree(formation);
            
            if (!json.has("program")) { 
                return "Error: the json need a field 'program'";
            }

            return chackProgram(json);
        }
        catch (JacksonException e) {
            return "Error: the json is not valid";
        }
    }

    @Tool("Invia formazione ai droni")
    public boolean sendFormationCommand(String formation) {
        System.out.println("inviando formazione ai droni:\n" + formation);
        return sender.sendFormation(formation);
    }

    private String chackProgram(JsonNode jsonFormation) {
        List<Formation> formations = formationProvider.getFormations();
        
        // Check if there a formation in the file with this name 
        Optional<Formation> f = formations.stream().filter(formation -> formation.getLabel() == jsonFormation.get("program").asText()).findAny();
        if (f.isEmpty()) {
            return "Error: no Formations with this program name";
        }
        
        Set<String> requiredFields = f.get().getParameters().keySet();
        Set<String> jsonFields = new HashSet<>();
        
        jsonFormation.fieldNames().forEachRemaining(jsonFields::add);
        
        // Check if the jsonFields are all the required fields
        if (!jsonFields.equals(requiredFields)) {
            Set<String> extra = new HashSet<>(jsonFields);
            extra.removeAll(requiredFields);
            
            Set<String> missing = new HashSet<>(requiredFields);
            missing.removeAll(jsonFields);
            
            if (!extra.isEmpty()) {
                return "Error: extra fields in the json, " + extra;
            }
            if (!missing.isEmpty()) {
                return "Error: missing fields in the json, " + missing;
            }
        }
        
        return "The json is VALID";
    }
}
