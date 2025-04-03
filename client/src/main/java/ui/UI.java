package ui;

import client.ServerFacade;
import model.Auth;

public class UI {
    protected static final String TEXT_COLOR_DEFAULT = EscapeSequences.SET_TEXT_COLOR_GREEN;
    protected static final String TEXT_COLOR_ALT = EscapeSequences.SET_TEXT_COLOR_YELLOW;
    protected static final String TEXT_COLOR_ERROR = EscapeSequences.SET_TEXT_COLOR_RED;

    protected State state;
    protected Auth auth;

    protected final ServerFacade serverFacade = new ServerFacade("http://localhost:8080/");

    public State getState() {
        return this.state;
    }

    public Auth getAuth() {
        return this.auth;
    }

    protected boolean checkLength(String[] command, int length) {
        if (command.length < length) {
            System.out.print(TEXT_COLOR_ERROR);
            System.out.print("not enough arguments \n");
            return false;
        }
        return true;
    }
}
