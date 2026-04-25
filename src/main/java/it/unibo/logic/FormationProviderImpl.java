package it.unibo.logic;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class FormationProviderImpl implements FormationProvider{
    private static final String FILE_NAME = "formation_param_descriptions.json"; 

    private ObjectMapper mapper;
    private List<Formation> formations = new LinkedList<Formation>();

    public FormationProviderImpl() {
        mapper = new ObjectMapper();
    }

    private List<Formation> readFormationsFromFile() {
        List<Formation> formationsReaded = new ArrayList<>();
    
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(FILE_NAME);

            Map<String, Formation> formationsMap = mapper.readValue( inputStream, new TypeReference<Map<String, Formation>>() {} );
            formationsMap.forEach((key, formation) -> {
                formation.setName(key);
                formationsReaded.add(formation);
            });
        } catch (IOException e) {
            System.out.println("error reading the json file (file name: " + FILE_NAME + ")\n" + e.getMessage());
        }

        return formationsReaded;
    }

    @Override
    public List<Formation> getFormations() {
        formations = readFormationsFromFile();
        return formations;
    }

    @Override
    public String getFormationsJson() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(FILE_NAME);
        
            if (inputStream == null) {
                System.out.println("File not found: " + FILE_NAME);
                return "";
            }
            
            String json = new String(inputStream.readAllBytes());
            inputStream.close();
            return json;
        } catch (IOException e) {
            System.out.println("Error reading the json file (file name: " + FILE_NAME + ")\n" + e.getMessage());
            return "";
        }
    }
}
