package it.unibo.logic;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class ChatModelFactory {

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
    
    private String getApiKey(String apiKeyCode) {
        String apiKey = System.getenv(apiKeyCode);
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("API_KEY not configurated!");
        }
        return apiKey;
    }
}
