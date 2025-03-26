package ui;

import client.Client;
import model.*;

import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;

public class PostloginUI {
    private static final String textColorDefault = EscapeSequences.SET_TEXT_COLOR_GREEN;
    private static final String textColorAlt = EscapeSequences.SET_TEXT_COLOR_YELLOW;
    private static final String textColorError = EscapeSequences.SET_TEXT_COLOR_RED;

    private State state;
    private Auth auth;
    private Game game;

    public PostloginUI(Auth auth) {
        this.state = State.postlogin;
        this.auth = auth;
    }

    public void start(State state) {
        this.state = state;
        while (Objects.equals(this.state, State.postlogin)) {
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
                case "create":
                case "Create": {
                    if (checkLength(command, 2)) {
                        String gameName = command[1];
                        Object obj = Client.createGame(gameName, this.auth);
                        if (obj instanceof Game game) {
                            System.out.print(textColorDefault);
                            System.out.print("Game successfully created! \n");
                            this.game = game;
//                            this.state = State.game;
                        } else {
                            System.out.print(textColorError);
                            System.out.print("Error creating game: ");
                            System.out.println(obj);
                        }
                    }
                    break;
                }
                case "logout":
                case "Logout": {
                    Object obj = Client.logout(this.auth);
                    if (obj.toString().equals("{}")) {
                        System.out.print(textColorDefault);
                        System.out.print("Successfully logged out! \n");
                        this.state = State.pregame;
                        this.auth = null;
                    } else {
                        System.out.print(textColorError);
                        System.out.print("Error logging out: ");
                        System.out.println(obj);
                    }
                    break;
                }
                case "list":
                case "List": {
                    Object obj = Client.getGames(this.auth); {
                        System.out.print(textColorDefault);
                        System.out.print("Games: \n");
                        System.out.print(textColorAlt);
                        System.out.print(obj);
                    }
                    break;
                }
                case "quit":
                case "Quit": {
                    this.state = State.stop;
                    System.out.print(textColorDefault);
                    System.out.print("Exiting program \n");
                    break;
                }
//                case "state":
//                    System.out.print(textColorAlt);
//                    System.out.println(this.state);
//                    break;
//                case "auth":
//                    System.out.print(textColorAlt);
//                    System.out.println(this.auth);
//                    break;
                case null, default: {
                    System.out.print(textColorError);
                    System.out.println("Unrecognized command. Type Help for help");
                    break;
                }
            }
        }
    }

    public State getState() {
        return this.state;
    }

    public Game getGame() {
        return this.game;
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
