package ui;

import client.Client;
import model.Auth;

import java.util.Objects;
import java.util.Scanner;

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

            System.out.print(textColorDefault);
            System.out.print("Type Help to get started>>> ");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var command = line.split(" ");

            switch(command[0]) {
                case "help":
                case "Help": {
                    System.out.print(textColorAlt);
                    System.out.print("create <Name>");
                    System.out.print(textColorDefault);
                    System.out.print(" - creates a new chess game \n");

                    System.out.print(textColorAlt);
                    System.out.print("list");
                    System.out.print(textColorDefault);
                    System.out.print(" - lists all chess games \n");

                    System.out.print(textColorAlt);
                    System.out.print("join <ID> [BLACK|WHITE]");
                    System.out.print(textColorDefault);
                    System.out.print(" - join a chess game \n");

                    System.out.print(textColorAlt);
                    System.out.print("spectate <ID>");
                    System.out.print(textColorDefault);
                    System.out.print(" - spectate a chess game \n");

                    System.out.print(textColorAlt);
                    System.out.print("logout");
                    System.out.print(textColorDefault);
                    System.out.print(" - logs out of your chess account \n");

                    System.out.print(textColorAlt);
                    System.out.print("quit");
                    System.out.print(textColorDefault);
                    System.out.print(" - close this program \n");
                    break;
                }
                case "quit":
                case "Quit": {
                    this.state = "stop";
                    System.out.print(textColorDefault);
                    System.out.print("Exiting program \n");
                    break;
                }
                case null, default: {
                    System.out.print(textColorError);
                    System.out.println("Unrecognized command. Type Help for help");
                    break;
                }
            }
        }
    }

    public String getState() {
        return this.state;
    }

    private boolean checkLength(String[] command, int length) {
        if (command.length < length) {
            System.out.print(textColorError);
            System.out.print("not enough arguments \n");
            return false;
        }
        return true;
    }
}
