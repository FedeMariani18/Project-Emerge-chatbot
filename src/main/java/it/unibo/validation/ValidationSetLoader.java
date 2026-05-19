package it.unibo.validation;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ValidationSetLoader {
    private final static String VALIDATION_SET_FILE_NAME = "validation_set.json";

    public static List<TestQuestion> loadQuestions() {
        List<TestQuestion> questions = new LinkedList<>();
        try {
            InputStream inputStream = ValidationSetLoader.class
                .getClassLoader()
                .getResourceAsStream(VALIDATION_SET_FILE_NAME);
            
            ObjectMapper mapper = new ObjectMapper();
            
            TestQuestion[] validationSet = mapper.readValue(inputStream, TestQuestion[].class);
            for (TestQuestion q: validationSet ) {
                questions.add(q);
            }

        } catch (IOException e) {
            System.out.println("error reading the json file (file name: " + VALIDATION_SET_FILE_NAME + ")\n" + e.getMessage());
        }

        return questions;
    }
}