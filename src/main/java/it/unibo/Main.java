package it.unibo;

import it.unibo.controller.Controller;
import it.unibo.logic.Model;
import it.unibo.logic.ModelImpl;
import it.unibo.view.ChatView;
import it.unibo.view.ChatWindow;

public class Main {
    public static void main(String[] args) {
        Model model = new ModelImpl();
        ChatView view = new ChatWindow();
        Controller controller = new Controller(view, model);
        controller.start();
    }
}
