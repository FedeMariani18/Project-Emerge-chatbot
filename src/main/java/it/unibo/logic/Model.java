package it.unibo.logic;

public interface Model {

    String askAgent(String message) throws Exception;
    
    void close();
}
