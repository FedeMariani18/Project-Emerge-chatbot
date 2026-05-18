package it.unibo.logic;

import java.time.Duration;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class ChatModelFactory {

    public ChatModel createChatModel(ModelProvider model) {
        String m = model.getName();

        switch (model.getProviderType()) {
            case GEMINI:
                return createGeminiChatModel(m);

            case OPENAI:
                return createOpenaiChatModel(m);                

            case OLLAMA:
                return createOllamaChatModel(m);    

            default:
                return createGeminiChatModel();
        }
    }

    public ChatModel createGeminiChatModel(String modelName){
        String apiKey = getApiKey("GEMINI_API_KEY");
        
        return GoogleAiGeminiChatModel.builder()
            .apiKey(apiKey)
            .modelName(modelName)
            .build();
    }

    public ChatModel createGeminiChatModel(){        
        return createGeminiChatModel(ModelProvider.GEMINI_DEFAULT.getName());
    }

    public ChatModel createOpenaiChatModel(String modelName){
        String apiKey = getApiKey("OPENAI_API_KEY");

        return OpenAiChatModel.builder()
            .apiKey(apiKey)
            .modelName(modelName)
            .build();
    }

    public ChatModel createOpenaiChatModel(){
        return createOpenaiChatModel(ModelProvider.OPENAI_DEFAULT.getName());
    }

    public ChatModel createOllamaChatModel(String modelName){
        
        return OllamaChatModel.builder()
            .baseUrl("http://127.0.0.1:11434")
            .modelName(modelName)
            .timeout(Duration.ofMinutes(10))
            .build();
    }

    public ChatModel createOllamaChatModel() {
        return createOllamaChatModel(ModelProvider.OLLAMA_QWEN.getName());
    }
    
    private String getApiKey(String apiKeyCode) {
        String apiKey = System.getenv(apiKeyCode);
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("API_KEY not configurated!");
        }
        return apiKey;
    }


}
