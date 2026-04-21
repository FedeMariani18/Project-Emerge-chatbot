package it.unibo;

import it.unibo.controller.Controller;
import it.unibo.logic.FormationProvider;
import it.unibo.logic.FormationProviderImpl;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.start();
    }
}
