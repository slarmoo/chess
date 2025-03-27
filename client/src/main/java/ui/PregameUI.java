package ui;
import client.ServerFacade;
import model.Auth;

import java.util.Objects;
import java.util.Scanner;

public class PregameUI {
    private static final String TEXT_COLOR_DEFAULT = EscapeSequences.SET_TEXT_COLOR_GREEN;
    private static final String TEXT_COLOR_ALT = EscapeSequences.SET_TEXT_COLOR_YELLOW;
    private static final String TEXT_COLOR_ERROR = EscapeSequences.SET_TEXT_COLOR_RED;

    private State state;
    private Auth auth;

    private final ServerFacade serverFacade = new ServerFacade("http://localhost:8080/");

    public PregameUI() {
        this.state = State.pregame;
    }

    public void start(State state) {
        this.state = state;
        while (Objects.equals(this.state, State.pregame)) {
            System.out.print(TEXT_COLOR_DEFAULT);
            System.out.print("Type Help to get started>>> ");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var command = line.split(" ");

            switch(command[0]) {
                case "help":
                case "Help": {
                    System.out.print(TEXT_COLOR_ALT);
                    System.out.print("register <USERNAME> <PASSWORD> <EMAIL>");
                    System.out.print(TEXT_COLOR_DEFAULT);
                    System.out.print(" - create your chess account \n");

                    System.out.print(TEXT_COLOR_ALT);
                    System.out.print("login <USERNAME> <PASSWORD>");
                    System.out.print(TEXT_COLOR_DEFAULT);
                    System.out.print(" - login to your chess account \n");

                    System.out.print(TEXT_COLOR_ALT);
                    System.out.print("help");
                    System.out.print(TEXT_COLOR_DEFAULT);
                    System.out.print(" - help with what commands you can use \n");

                    System.out.print(TEXT_COLOR_ALT);
                    System.out.print("quit");
                    System.out.print(TEXT_COLOR_DEFAULT);
                    System.out.print(" - close this program \n");
                    break;
                }
                case "register":
                case "Register": {
                    this.register(command);
                    break;
                }
                case "login":
                case "Login": {
                    this.login(command);
                    break;
                }
                case "quit":
                case "Quit": {
                    System.out.print(TEXT_COLOR_DEFAULT);
                    System.out.print("Exiting program \n");
                    this.state = State.stop;
                    break;
                }
                case null:
                default: {
                    System.out.print(TEXT_COLOR_ERROR);
                    System.out.println("Unrecognized command. Type Help for help");
                    break;
                }
            }
        }
    }

    public Auth getAuth() {
        return this.auth;
    }

    public State getState() {
        return this.state;
    }

    private void register(String[] command) {
        if(checkLength(command, 4)) {
            String username = command[1];
            String password = command[2];
            String email = command[3];
            Object obj = serverFacade.register(username, password, email);
            if(obj instanceof Auth auth) {
                System.out.print(TEXT_COLOR_DEFAULT);
                System.out.print("User successfully created! \n");
                this.state = State.postlogin;
                this.auth = auth;
            } else {
                System.out.print(TEXT_COLOR_ERROR);
                System.out.println("Error creating user");
            }
        }
    }

    private void login(String[] command) {
        if(checkLength(command, 3)) {
            String username = command[1];
            String password = command[2];
            Object obj = serverFacade.login(username, password);
            if(obj instanceof Auth auth) {
                System.out.print(TEXT_COLOR_DEFAULT);
                System.out.print("Successfully Logged In! \n");
                this.state = State.postlogin;
                this.auth = auth;
            } else {
                System.out.print(TEXT_COLOR_ERROR);
                System.out.println("Error logging in: incorrect credentials");
            }
        }
    }

    private boolean checkLength(String[] command, int length) {
        if (command.length < length) {
            System.out.print(TEXT_COLOR_ERROR);
            System.out.print("not enough arguments \n");
            return false;
        }
        return true;
    }
}
