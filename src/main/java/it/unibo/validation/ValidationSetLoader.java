package it.unibo.validation;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ValidationSetLoader {
    private final static String VALIDATION_SET_FILE_NAME = "validation_set.json";

    public static List<TestQuestion> loadQuestions() throws Exception {
        InputStream inputStream = ValidationSetLoader.class
            .getClassLoader()
            .getResourceAsStream(VALIDATION_SET_FILE_NAME);
        
        ObjectMapper mapper = new ObjectMapper();
        TestQuestion[] questions = mapper.readValue(inputStream, TestQuestion[].class);
        
        return Arrays.asList(questions);
    }
}