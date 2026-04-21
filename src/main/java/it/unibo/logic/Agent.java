package it.unibo.logic;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;

public class Agent {

    private static final String API_KEY = System.getenv("GEMINI_API_KEY");
    private static final String SYSTEM_PROMPT = """
        Tu sei un assistente IA specializzato nel controllo di sciami di droni.
        Il tuo compito è guidare l'utente nella selezione della formazione più adatta alle sue esigenze.
        
        COMPORTAMENTO:
        - Sii cordiale, professionale e conciso
        - Poni domande per capire le necessità dell'utente (velocità, area di copertura, manovrabilità)
        - Suggerisci la formazione più idonea basandoti sulle loro esigenze
        - Una volta confermata la formazione, invia il comando ai droni utilizzando i tool disponibili
        
        PROCESSO DECISIONALE:
        1. Comprendi cosa vuole fare l'utente
        2. Consulta le formazioni disponibili tramite il tool getAvailableFormations()
        3. Valida la scelta con validateFormation()
        4. Invia il comando tramite sendFormationCommand()
        
        LINEE GUIDA:
        - Se l'utente chiede quali formazioni sono disponibili, usa il tool getAvailableFormations()
        - Se proponi una formazione, spiega brevemente i suoi vantaggi
        - Se l'utente non è sicuro, offri consigli basati sul caso d'uso
        - Chiedi sempre all'utente conferma prima di inviare un comando ai droni
        - Se ricevi un errore, comunica all'utente e offri alternative
        
        TONE: Professionale ma amichevole, tecnico ma comprensibile
        """;

    interface Assistant {
        @SystemMessage(SYSTEM_PROMPT)
        String chat(String message);
    }

    private ToolsHandler tools;
    private Assistant assistant;

    public Agent(ToolsHandler tools) {
        this.tools = tools;

        if (API_KEY == null || API_KEY.isEmpty()) {
            System.err.println("ERRORE: La variabile d'ambiente GEMINI_API_KEY non è configurata!");
        } else {
            System.out.println(API_KEY);
        }

        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        
        ChatModel model = GoogleAiGeminiChatModel.builder()
            .apiKey(API_KEY)
            .modelName("gemini-2.5-flash-lite")
            .build();

        assistant = AiServices.builder(Assistant.class)
            .chatModel(model)
            .chatMemory(chatMemory)
            .tools(tools)
            .build();
    }

    public String chat(String message) {
        return assistant.chat(message);
    }
}