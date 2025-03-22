package ui;

import model.Auth;

import java.util.Objects;

public class PostloginUI {
    private static final String textColorDefault = EscapeSequences.SET_TEXT_COLOR_GREEN;
    private static final String textColorAlt = EscapeSequences.SET_TEXT_COLOR_YELLOW;
    private static final String textColorError = EscapeSequences.SET_TEXT_COLOR_RED;

    private String state;
    private final Auth auth;

    public PostloginUI(Auth auth) {
        this.state = "postlogin";
        this.auth = auth;
    }

    public void start() {
        while (Objects.equals(state, "postlogin")) {

        }
    }
}
