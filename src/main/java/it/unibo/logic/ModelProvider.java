package it.unibo.logic;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ModelProvider {
    OLLAMA_MISTRAL("mistral", ProviderType.OLLAMA),
    OLLAMA_QWEN("qwen2.5:3b", ProviderType.OLLAMA),
    OLLAMA_GEMMA4("gemma4:latest", ProviderType.OLLAMA),
    OLLAMA_MISTRAL_7B("mistral:7b-instruct", ProviderType.OLLAMA),
    
    GEMINI_2_5_FLASH("gemini-2.5-flash", ProviderType.GEMINI),
    GEMINI_2_5_FLASH_LITE("gemini-2.5-flash-lite", ProviderType.GEMINI),
    GEMINI_2_5_PRO("gemini-2.5-pro", ProviderType.GEMINI),
    GEMINI_DEFAULT(GEMINI_2_5_FLASH.getName(), ProviderType.GEMINI),
    
    OPENAI_GPT_4O_MINI("gpt-4o-mini", ProviderType.OPENAI),
    OPENAI_GPT_5_1("gpt-5.1", ProviderType.OPENAI),
    OPENAI_GPT_4_1_MINI("gpt-4.1-mini", ProviderType.OPENAI),
    OPENAI_DEFAULT(OPENAI_GPT_4O_MINI.getName(), ProviderType.OPENAI);

    public enum ProviderType {
        GEMINI, OPENAI, OLLAMA
    }

    private String name;
    private ProviderType providerType;
    
    ModelProvider(String name, ProviderType providerType) {
        this.name = name;
        this.providerType = providerType;
    }

    @JsonValue
    public String getName() {
        return this.name;
    }

    public ProviderType getProviderType() { 
        return this.providerType; 
    }

    public static ModelProvider fromString(String provider) {
        try {
            return ModelProvider.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            return GEMINI_2_5_FLASH;  // Default
        }
    }
}
