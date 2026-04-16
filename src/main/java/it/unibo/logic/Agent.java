package it.unibo.logic;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;

public class Agent {

    private static final String SYSTEM_PROMPT = """
        Sei un ChatBot per la conversione di linguaggio umano, in formazioni per uno sciame di droni.
        Guida l'utente nella scelta della formazione più consona tra quelle disponibili per le sue esigenze
        
        Hai a disposizione dei Tool per effettuare diverse azioni.
        """;

    interface Assistant {
        String chat(String message);
    }

    private Assistant assistant;

    public Agent() {
        
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        
        ChatModel model = GoogleAiGeminiChatModel.builder()
            .apiKey("AIzaSyCRnw0ydsMHkUgFyalbcSF5bb2y0zxlVEE")
            .modelName("gemini-2.5-flash-lite")
            .build();

        assistant = AiServices.builder(Assistant.class)
            .chatModel(model)
            .chatMemory(chatMemory)
            .build();
    }

    @SystemMessage(SYSTEM_PROMPT)
    public String chat(String message) {
        return assistant.chat(message);
    }
}