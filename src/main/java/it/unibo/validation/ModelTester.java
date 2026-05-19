package it.unibo.validation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.service.Result;
import it.unibo.common.Pair;
import it.unibo.logic.Agent;
import it.unibo.logic.FormationProvider;
import it.unibo.logic.ToolsHandler;
import it.unibo.logic.Sender;
import it.unibo.logic.ModelProvider;

public class ModelTester {

    private static final int N_ATTEMPTS = 10;

    private Agent agent;
    private ToolsHandler mockTools;
    private List<ModelProvider> models;
    private Map<ModelProvider, TestData> testResult; // modelProvider -> questions<(accuracy, avgTime)>
    private List<TestQuestion> testQuestions;

    @SuppressWarnings("null")
    public ModelTester() {
        FormationProvider formationProvider = new StubFormationProvider();
        Sender sender = new StubSender();

        this.models = Arrays.stream(ModelProvider.values()).toList();

        this.mockTools = new ToolsHandler(formationProvider, sender);
        
        this.agent = new Agent(mockTools);
        
        testResult = new HashMap<ModelProvider,TestData>();

        this.testQuestions = ValidationSetLoader.loadQuestions();
    }

    public void test() {
        final int TOTAL_ATTEMPTS = (N_ATTEMPTS * testQuestions.size());
        
        Float nTotalCorrectResponse = 0f;
        long totalTime = 0l;
        Float nCorrectResponse = 0f;
        long time = 0l;

        for(ModelProvider model : models) {
            
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
                }

                nTotalCorrectResponse += nCorrectResponse;
                totalTime += time;
                testResult.get(model).addQuestionResult(
                    new Pair<Float, Long>(nCorrectResponse / N_ATTEMPTS, time / N_ATTEMPTS)
                );

                System.out.println(String.format("====> Domanda: %d | Accuratezza: %.2f%% | Tempo medio: %d ms\n", 
                model.name(), question.getId(), (nCorrectResponse / N_ATTEMPTS) * 100, time / N_ATTEMPTS));
            }

            testResult.get(model).setTotalAccuracy(nTotalCorrectResponse / TOTAL_ATTEMPTS);
            testResult.get(model).setAvgResponseTime(totalTime / TOTAL_ATTEMPTS);

            System.out.println(String.format("========> MODELLO: %s | Accuratezza Totale: %.2f%% | Tempo Medio: %d ms <========", 
                model.name(), testResult.get(model).getTotalAccuracy() * 100, testResult.get(model).getAvgResponseTime()));

            System.out.println("==================================================================================================");
        }
    }

    public Map<ModelProvider, TestData> getTestResult() {
        return this.testResult;
    }
}
