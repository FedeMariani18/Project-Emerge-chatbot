package it.unibo.logic;

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
    public boolean validateFormation(String formation) {
        System.out.println("validazione formazione:\n " + formation);
        
        try {
            JsonNode json = objMapper.readTree(formation);
            
            if (!json.has("program")) {
                return false;
            }
        }
        catch (JacksonException e) {
            System.out.println("Not valid formation: " + e.getMessage());
            return false;
        }
        return true;
    }

    @Tool("Invia formazione ai droni")
    public boolean sendFormationCommand(String formation) {
        System.out.println("inviando formazione ai droni:\n" + formation);
        return sender.sendFormation(formation);
    }


}
