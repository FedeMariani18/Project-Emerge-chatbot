package it.unibo.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import it.unibo.common.ModelDetail;
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
                
                List<ModelDetail> questions = data.getQuestionsResult();
                for (int i = 0; i < questions.size(); i++) {
                    ModelDetail qResult = questions.get(i);
                    writer.write(String.format("| Domanda %2d | %10.2f%% | %16d |\n", 
                        (i + 1), qResult.getAccuracy() * 100, qResult.getAvgTime()));
                }
                writer.write("\n--------------------------------------------------\n\n");
            }
            System.out.println("[REPORT] Report testuale salvato con successo in: " + new File(filePath).getAbsolutePath());
        } catch (IOException e) {
            System.err.println("[ERRORE] Impossibile scrivere il report testuale: " + e.getMessage());
        }
    }

    public void writeReports(Map<ModelProvider, TestData> testResult, String JsonPath, String textPath) {
        this.writeAsJson(testResult, JsonPath);
        this.writeAsReadableText(testResult, textPath);
    }

    /**
     * Pulisce o cancella i file se esistono già, per iniziare una sessione di test da zero.
     * Da chiamare una sola volta nel costruttore o all'inizio del metodo test().
     */
    public void cleanFiles(String jsonPath, String textPath) {
        try {
            File jsonFile = new File(jsonPath);
            File textFile = new File(textPath);
            
            if (jsonFile.exists() && jsonFile.delete()) {
                System.out.println("[CLEAN] Vecchio file JSON rimosso: " + jsonPath);
            }
            if (textFile.exists() && textFile.delete()) {
                System.out.println("[CLEAN] Vecchio file Markdown rimosso: " + textPath);
            }
            
            // Crea un'intestazione iniziale pulita per il file di testo
            try (FileWriter writer = new FileWriter(textFile, false)) {
                writer.write("# BENCHMARK REPORT - AGENT VALIDATION (INCREMENTAL)\n\n");
            }
        } catch (IOException e) {
            System.err.println("[ERRORE CLEAN] Impossibile inizializzare i file: " + e.getMessage());
        }
    }

    /**
     * Appende i dati di un SINGOLO modello nel file di testo/Markdown.
     * Usa il flag 'true' nel FileWriter per non sovrascrivere quello che c'era prima.
     */
    public void appendReadableText(ModelProvider model, TestData data, String filePath) {
        // Il flag 'true' indica al FileWriter di aggiungere in coda (append)
        try (FileWriter writer = new FileWriter(filePath, true)) {
            
            writer.write(String.format("## Modello: %s\n", model.name()));
            writer.write(String.format("- **Accuratezza Totale**: %.2f%%\n", data.getTotalAccuracy() * 100));
            writer.write(String.format("- **Tempo Medio Risposta**: %d ms\n\n", data.getAvgResponseTime()));
            
            writer.write("| ID Domanda | Accuratezza | Tempo Medio (ms) |\n");
            writer.write("|------------|-------------|------------------|\n");
            
            List<ModelDetail> questions = data.getQuestionsResult();
            for (int i = 0; i < questions.size(); i++) {
                ModelDetail qResult = questions.get(i);
                writer.write(String.format("| Domanda %2d | %10.2f%% | %16d |\n", 
                    (i + 1), qResult.getAccuracy() * 100, qResult.getAvgTime()));
            }
            writer.write("\n--------------------------------------------------\n\n");
            
            // Forza il salvataggio immediato sul disco fisso
            writer.flush(); 
            System.out.println("[REPORT] Risultati parziali (Testo) aggiornati per " + model.name());
        } catch (IOException e) {
            System.err.println("[ERRORE APPEND TESTO] Impossibile scrivere in coda: " + e.getMessage());
        }
    }

    /**
     * Aggiorna in modo sicuro il file JSON salvando lo stato corrente della mappa dei risultati.
     * Riscrive l'intera mappa aggiornata fino a quel momento ad ogni fine ciclo di un modello.
     * Questo garantisce che il file JSON sia SEMPRE sintatticamente valido e leggibile da programmi esterni.
     */
    public void appendJson(Map<ModelProvider, TestData> currentTestResult, String filePath) {
        try {
            File file = new File(filePath);
            // Scrive l'intero set di dati accumulato fino a questo momento
            objectMapper.writeValue(file, currentTestResult);
            System.out.println("[REPORT] Risultati parziali (JSON) aggiornati su file.");
        } catch (IOException e) {
            System.err.println("[ERRORE APPEND JSON] Impossibile aggiornare il JSON: " + e.getMessage());
        }
    }
}