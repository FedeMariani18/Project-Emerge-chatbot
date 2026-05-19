package it.unibo.validation;

import it.unibo.logic.Sender;

public class StubSender implements Sender {

    @Override
    public boolean sendFormation(String formation) { return true; }

    @Override
    public boolean connect() { return true; }

    @Override
    public boolean disconnect() { return true; }

    @Override
    public boolean isConnected() { return true; }
    
}
