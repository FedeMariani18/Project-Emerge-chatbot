package it.unibo.validation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.exception.HttpException;
import dev.langchain4j.service.Result;
import it.unibo.common.ModelDetail;
import it.unibo.common.Pair;
import it.unibo.logic.Agent;
import it.unibo.logic.FormationProvider;
import it.unibo.logic.ToolsHandler;
import it.unibo.logic.Sender;
import it.unibo.logic.ModelProvider;

public class ModelTester {
    private static final int N_ATTEMPTS = 10;
    private static final String REPORT_PATH = "report/output_benchmark"; 
    private static final String JSON_PATH = REPORT_PATH + ".json";
    private static final String TEXT_PATH = REPORT_PATH + ".md";

    private Agent agent;
    private ToolsHandler mockTools;
    private List<ModelProvider> models;
    private Map<ModelProvider, TestData> testResult; // modelProvider -> questions<(accuracy, avgTime)>
    private List<TestQuestion> testQuestions;
    private TestReportWriter writer;

    @SuppressWarnings("null")
    public ModelTester() {
        FormationProvider formationProvider = new StubFormationProvider();
        Sender sender = new StubSender();

        this.models = Arrays.stream(ModelProvider.values()).toList();
        // to check only local models (by OLLAMA)
        // this.models = this.models.stream().filter(m -> m.getProviderType().equals(ModelProvider.ProviderType.OLLAMA)).toList();

        this.mockTools = new ToolsHandler(formationProvider, sender);
        
        this.agent = new Agent(mockTools);
        
        testResult = new HashMap<ModelProvider,TestData>();

        this.testQuestions = ValidationSetLoader.loadQuestions();
    
        this.writer = new TestReportWriter();
        writer.cleanFiles(JSON_PATH, TEXT_PATH);
    }

    /**
     * It make a test on all the available model and check if they use the right tools,
     * based on a validation set of questions and expected tools
     */
    public void test() {
        final int TOTAL_ATTEMPTS = (N_ATTEMPTS * testQuestions.size());
        
        Float nTotalCorrectResponse = 0f;
        long totalTime = 0l;
        Float nCorrectResponse = 0f;
        long time = 0l;

        for(ModelProvider model : models) {
            if(model.name().toUpperCase().contains("DEFAULT")) { continue; }    // Skipping the Default models (a repetition of other models)

            nTotalCorrectResponse = 0f;
            totalTime = 0l;

            agent.changeModel(model);
            testResult.put(model, new TestData());
            System.out.println("====================== MODEL: " + model.getName() + " ======================");

            for(TestQuestion question : testQuestions) {
                    
                nCorrectResponse = 0f;
                time = 0l;

                for(int i = 0; i < N_ATTEMPTS; i++) {
                    agent.resetMemory();

                    long startTime = System.currentTimeMillis();

                    try {
                        Result<String> res = agent.chat(UserMessage.from(question.getQuestion()));
                        long elapsedTime = System.currentTimeMillis() - startTime; 

                        Set<String> usedTools = res.toolExecutions() != null 
                            ? res.toolExecutions().stream().map(tool -> tool.request().name()).collect(Collectors.toSet())
                            : new HashSet<>();

                        Set<String> expectedTools = question.getExpectedToolList();
                        if (usedTools.equals(expectedTools)) {
                            nCorrectResponse += 1;
                        }
                        time += elapsedTime;
                    } catch (Exception e) {
                        time += (System.currentTimeMillis() - startTime);
                        
                        System.err.println(String.format("[ERRORE BI PASSATO] Modello %s | Domanda %d | Tentativo %d fallito per sovraccarico API. Salto...", 
                        model.name(), question.getId(), i));
                    
                        if(e.getClass().equals(HttpException.class)) {
                            // If the problem come from an on-cloud model, we let the program rest for few seconds
                            try { Thread.sleep(1500); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                        }
                    }
                }

                nTotalCorrectResponse += nCorrectResponse;
                totalTime += time;
                testResult.get(model).addQuestionResult(
                    new ModelDetail(nCorrectResponse / N_ATTEMPTS, time / N_ATTEMPTS)
                );

                System.err.println("====> Domanda: " + question.getId() + 
                    formattedOutput((nCorrectResponse / N_ATTEMPTS) * 100, time / N_ATTEMPTS));
            }

            testResult.get(model).setTotalAccuracy(nTotalCorrectResponse / TOTAL_ATTEMPTS);
            testResult.get(model).setAvgResponseTime(totalTime / TOTAL_ATTEMPTS);

            System.out.println("========> MODELLO: " + model.name() + 
                formattedOutput(testResult.get(model).getTotalAccuracy() * 100, testResult.get(model).getAvgResponseTime()) +
                "\n==================================================================================================");
        
            writer.appendJson(testResult, JSON_PATH);
            writer.appendReadableText(model, testResult.get(model), TEXT_PATH);
        }
    }

    /**
     * @return a map (model -> reults), that contains all the data funded with the method test()
     */
    public Map<ModelProvider, TestData> getTestResult() {
        return this.testResult;
    }

    private String formattedOutput(Float accuracy, Long avgTime){
        return String.format(" | Accuratezza: %.2f%% | Tempo medio: %d %s\n", 
                    accuracy, 
                    (avgTime > 1000? avgTime/1000 : avgTime), 
                    (avgTime > 1000? "s" : "ms"));
    }
}
