package it.unibo.logic;

import dev.langchain4j.agent.tool.Tool;

public class ToolsHandler {
    private FormationProvider formationProvider;
    private Sender sender;

    public ToolsHandler(FormationProvider formationProvider, Sender sender) {
        this.formationProvider = formationProvider;
        this.sender = sender;
    }

    @Tool("Ottieni una stringa json tutte le formazioni disponibili per i droni")
    public String getAvailableFormation(){
        return formationProvider.getFormationsJson();
    }

    @Tool("Valida la formazione che hai creato prima di inviarla ai droni")
    public boolean validateFormation(String formation) {
        System.out.println("validazione formazione:\n " + formation);
        return true;
    }

    @Tool("Invia formazione ai droni")
    public boolean sendFormation(String formation) {
        //return sender.sendFormation(formation, formation);
        System.out.println("inviando formazione ai droni:\n" + formation);
        return true; 
    }


}
