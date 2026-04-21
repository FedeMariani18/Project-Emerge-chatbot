package it.unibo.logic;

import java.util.List;

public interface FormationProvider {
    
    /**
     * Read from file all the formations and parse it into Java objects
     * @return a List of Formation that reppresents the content of the file with the formations
     */
    public List<Formation> getFormations();

    /**
     * Read from file all the formations
     * @return a String with the exact content of the json file
     */
    public String getFormationsJson();
}
