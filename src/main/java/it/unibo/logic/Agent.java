package it.unibo.logic;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;

public class Agent {

    //#region SYSTEM_PROMPT
    private static final String SYSTEM_PROMPT = """
        Tu sei un assistente IA specializzato nel controllo di sciami di droni.
        Il tuo compito è guidare l'utente nella selezione della formazione più adatta alle sue esigenze.
        
        UTENTE TARGET:
        L'utente non è specializzato sull'argomento quindi: 
        - Non utilizzare parole specifiche 
        - Non mostrare dettagli tecnici
        - Non mostrare JSON
        - Non parlare di tools
        - Non utilizzare inglese
        
        COMPORTAMENTO:
        - Sii cordiale, professionale e conciso
        - Poni domande per capire le necessità dell'utente (non chiedere mai parametri che in realtà non esistono)
        - Suggerisci la formazione più idonea basandoti sulle loro esigenze
        - Una volta confermata la formazione, invia il comando ai droni utilizzando i tool disponibili
        
        PROCESSO DECISIONALE:
        1. Comprendi cosa vuole fare l'utente
        2. Consulta le formazioni disponibili tramite il tool getAvailableFormations()
        3. Valida il json che hai generato con validateFormation() (non richiedere conferma per questa azione)
        4. Invia il comando tramite sendFormationCommand() (richiedi conferma per questa azione)
        
        LINEE GUIDA:
        - Se l'utente chiede quali formazioni sono disponibili, usa il tool getAvailableFormations()
        - Se proponi una formazione, spiega brevemente i suoi vantaggi
        - Chiedi sempre all'utente conferma prima di inviare un comando ai droni
        - Se ricevi un errore, comunica all'utente e offri alternative

        IMPORTANTE: Quando hai la conferma dell'utente per inviare la formazione usa il tool sendFormationCommand(formation)
        il parametro formation deve essere una stringa con ESCLUSIVAMENTE un testo JSON con questo formato:
        {
        "program": "nomedellaformazione",
        "parametro1": valore,
        "parametro2": valore
        }
        
        Esempio per V Shape:
        {
        "program": "vShape",
        "interDistanceV": 0.4,
        "angleV": -0.785,
        "collisionArea": 0.3,
        "stabilityThreshold": 0.1
        }

        (ricordati che per la collisionArea e per lo stabilityThreshold i valori di default sono sempre 0.3 e 0.1)
        
        TONE: Professionale ma amichevole, tecnico ma comprensibile
        """;
    //#endregion

    // interface to build our AiService of Langchain4j
    interface Assistant {
        @SystemMessage(SYSTEM_PROMPT)
        String chat(String message);

        @SystemMessage(SYSTEM_PROMPT)
        Result<String> chat(UserMessage message);
    }

    private static final int STANDARD_MEMORY_WINDOW = 10;
    private static final ModelProvider STANDARD_MODEL = ModelProvider.GEMINI_2_5_FLASH;

    private ChatModel model;
    private ToolsHandler tools;
    private int currentMemorySize;
    private Assistant assistant;
    private ChatMemory chatMemory;

    private ChatModelFactory factory = new ChatModelFactory();

    public Agent(ToolsHandler tools) {
        this(tools, STANDARD_MODEL, STANDARD_MEMORY_WINDOW);
    }

    public Agent(ToolsHandler tools, ModelProvider modelProvider) {
        this(tools, modelProvider, STANDARD_MEMORY_WINDOW);
    }

    public Agent(ToolsHandler tools, ModelProvider modelProvider, int sizeMemoryWindow) {
        this.tools = tools;
        this.currentMemorySize = sizeMemoryWindow;
        this.model = factory.createChatModel(modelProvider);
        this.chatMemory = MessageWindowChatMemory.withMaxMessages(this.currentMemorySize);

        this.tools.setOnResetMemoryCallback(() -> this.resetMemory());

        this.assistant = buildAssistant();
    }

    /**
     * Rebuild the assistance with all his components (model, chatMemory, tools)
     * @return the instance of the Assistant which is an AiService of LangChain4j 
     */
    private Assistant buildAssistant() {
        return AiServices.builder(Assistant.class)
            .chatModel(model)
            .chatMemory(chatMemory)
            .tools(tools)
            .build();
    }

    public String chat(String message) {
        return assistant.chat(message);
    }

    public Result<String> chat(UserMessage message) {
        return assistant.chat(message);
    }

    /**
     * Change the model to wich the agent makes requests 
     * @param modelProvider a model from the enum ModelProvider
     */
    public void changeModel(ModelProvider modelProvider) {
        this.model = factory.createChatModel(modelProvider);
        this.assistant = buildAssistant();
    }

    /**
     * Reset and change the dimension of the memory, after this the agent wiil not remind anything
     * @param newSizeMemoryWindow the number of message that the agent will going to remember
     */
    public void changeMemory(int newSizeMemoryWindow) {
        this.currentMemorySize = newSizeMemoryWindow;
        this.chatMemory = MessageWindowChatMemory.withMaxMessages(newSizeMemoryWindow);
        this.assistant = buildAssistant();
    }

    /**
     * Reset the memory of the agent but maintain the previouse window size
     */
    public void resetMemory() {
        this.chatMemory = MessageWindowChatMemory.withMaxMessages(this.currentMemorySize);
        this.assistant = buildAssistant();
    }
}