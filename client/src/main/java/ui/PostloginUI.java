package ui;

import chess.ChessGame;
import client.ServerFacade;
import model.*;

import java.util.Objects;
import java.util.Scanner;

public class PostloginUI {
    private static final String textColorDefault = EscapeSequences.SET_TEXT_COLOR_GREEN;
    private static final String textColorAlt = EscapeSequences.SET_TEXT_COLOR_YELLOW;
    private static final String textColorError = EscapeSequences.SET_TEXT_COLOR_RED;

    private State state;
    private Auth auth;
    private Game game = null;
    private ChessGame.TeamColor yourColor = ChessGame.TeamColor.WHITE;

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
                        Object obj = ServerFacade.createGame(gameName, this.auth);
                        if (obj instanceof Game game) {
                            System.out.print(textColorDefault);
                            System.out.print("Game successfully created! \n");
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
                    Object obj = ServerFacade.logout(this.auth);
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
                    String games = ServerFacade.getGames(this.auth);
                    if(games != null) {
                        System.out.print(textColorDefault);
                        System.out.print("Games: \n");
                        System.out.print(textColorAlt);
                        System.out.print(games);
                    } else {
                        System.out.print(textColorError);
                        System.out.println("Error grabbing games");
                    }
                    break;
                }
                case "join":
                case "Join": {
                    if(checkLength(command, 3)) {
                        int id;
                        try {
                            id = Integer.parseInt(command[1]);
                        } catch (NumberFormatException e) {
                            System.out.print(textColorError);
                            System.out.println("Not a number. Type Help for help");
                            break;
                        }
                        String colorString = command[2].toLowerCase();
                        ChessGame.TeamColor color = colorString.equals("black") ? ChessGame.TeamColor.BLACK :
                                colorString.equals("white") ? ChessGame.TeamColor.WHITE : null;
                        if(color == null) {
                            System.out.print(textColorError);
                            System.out.println("Unrecognized color. Type Help for help");
                            break;
                        }
                        Object obj = ServerFacade.joinGame(id, color, auth);
                        if (obj instanceof Game game) {
                            System.out.print(textColorDefault);
                            System.out.print("joined game \n");
                            this.state = State.game;
                            this.game = game;
                            this.yourColor = color;
                        } else {
                            System.out.print(textColorError);
                            System.out.println("Error trying to join game " + id);
                        }
                    }
                    break;
                }
                case "spectate":
                case "Spectate": {
                    if(checkLength(command, 2)) {
                        int id;
                        try {
                            id = Integer.parseInt(command[1]);
                        } catch (NumberFormatException e) {
                            System.out.print(textColorError);
                            System.out.println("Not a number. Type Help for help");
                            break;
                        }
                        try {
                            Game game = ServerFacade.grabGameWithID(id, this.auth);
                            if (game != null) {
                                System.out.print(textColorDefault);
                                System.out.print("joined game \n");
                                this.state = State.game;
                                this.game = game;
                            } else {
                                System.out.print(textColorError);
                                System.out.println("Error trying to spectate game " + id);
                            }
                        } catch (Exception e) {
                            System.out.print(textColorError);
                            System.out.println("Error trying to spectate game " + id);
                            break;
                        }
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
//                case "test:state":
//                    System.out.print(textColorAlt);
//                    System.out.println(this.state);
//                    break;
//                case "test:auth":
//                    System.out.print(textColorAlt);
//                    System.out.println(this.auth);
//                    break;
//                case "test:empty":
//                    ServerFacade.emptyDatabase();
//                    this.state = State.stop;
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

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public ChessGame.TeamColor getColor() {
        return this.yourColor;
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
