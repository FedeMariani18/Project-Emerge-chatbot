package it.unibo.validation;

import java.util.List;

import it.unibo.logic.Formation;
import it.unibo.logic.FormationProvider;

public class StubFormationProvider implements FormationProvider{

    @Override
    public List<Formation> getFormations() { return List.of(); }

    @Override
    public String getFormationsJson() { return ""; }
    
}
