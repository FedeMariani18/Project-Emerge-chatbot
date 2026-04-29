package it.unibo.logic;

public enum ModelProvider {
    GEMINI_2_5_FLASH("gemini-2.5-flash"),
    GEMINI_2_5_FLASH_LITE("gemini-2.5-flash-lite"),
    GEMINI_2_5_PRO("gemini-2.5-pro"),
    GEMINI_DEFAULT(GEMINI_2_5_FLASH.getName()),
    GPT_4O_MINI("gpt-4o-mini"),
    GPT_5_1("gpt-5.1"),
    GPT_4_1_MINI("gpt-4.1-mini"),
    OPENAI_DEFAULT(GPT_4O_MINI.getName());

    private String name;
    
    ModelProvider(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static ModelProvider fromString(String provider) {
        try {
            return ModelProvider.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            return GEMINI_2_5_FLASH;  // Default
        }
    }
}
