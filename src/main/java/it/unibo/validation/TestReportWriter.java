package it.unibo.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.unibo.common.Pair;
import it.unibo.logic.ModelProvider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TestReportWriter {

    private final ObjectMapper objectMapper;

    public TestReportWriter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void writeAsJson(Map<ModelProvider, TestData> testResult, String filePath) {
        try {
            File file = new File(filePath);
            objectMapper.writeValue(file, testResult);
            System.out.println("Results correctly reported in: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error: impossible to write the report: " + e.getMessage());
        }
    }

    public void writeAsReadableText(Map<ModelProvider, TestData> testResult, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("# BENCHMARK REPORT - AGENT VALIDATION\n\n");
            
            for (Map.Entry<ModelProvider, TestData> entry : testResult.entrySet()) {
                ModelProvider model = entry.getKey();
                TestData data = entry.getValue();

                writer.write(String.format("## Modello: %s\n", model.name()));
                writer.write(String.format("- **Accuratezza Totale**: %.2f%%\n", data.getTotalAccuracy() * 100));
                writer.write(String.format("- **Tempo Medio Risposta**: %d ms\n\n", data.getAvgResponseTime()));
                
                // Generazione di una tabella per i risultati delle singole domande
                writer.write("| ID Domanda | Accuratezza | Tempo Medio (ms) |\n");
                writer.write("|------------|-------------|------------------|\n");
                
                List<Pair<Float, Long>> questions = data.getQuestionsResult();
                for (int i = 0; i < questions.size(); i++) {
                    Pair<Float, Long> qResult = questions.get(i);
                    writer.write(String.format("| Domanda %2d | %10.2f%% | %16d |\n", 
                        (i + 1), qResult.getFirst() * 100, qResult.getSecond()));
                }
                writer.write("\n--------------------------------------------------\n\n");
            }
            System.out.println("[REPORT] Report testuale salvato con successo in: " + new File(filePath).getAbsolutePath());
        } catch (IOException e) {
            System.err.println("[ERRORE] Impossibile scrivere il report testuale: " + e.getMessage());
        }
    }
}