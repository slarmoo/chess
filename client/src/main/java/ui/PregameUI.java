package ui;
import client.Client;
import model.Auth;

import java.util.Objects;
import java.util.Scanner;

public class PregameUI {
    private static final String textColorDefault = EscapeSequences.SET_TEXT_COLOR_GREEN;
    private static final String textColorAlt = EscapeSequences.SET_TEXT_COLOR_YELLOW;
    private static final String textColorError = EscapeSequences.SET_TEXT_COLOR_RED;

    private String state;
    private Auth auth;

    public PregameUI() {
        this.state = "pregame";
    }

    public void start(String state) {
        this.state = state;
        while (Objects.equals(this.state, "pregame")) {
            System.out.print(textColorDefault);
            System.out.print("Type Help to get started>>> ");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var command = line.split(" ");

            switch(command[0]) {
                case "help":
                case "Help": {
                    System.out.print(textColorAlt);
                    System.out.print("register <USERNAME> <PASSWORD> <EMAIL>");
                    System.out.print(textColorDefault);
                    System.out.print(" - create your chess account \n");

                    System.out.print(textColorAlt);
                    System.out.print("login <USERNAME> <PASSWORD>");
                    System.out.print(textColorDefault);
                    System.out.print(" - login to your chess account \n");

                    System.out.print(textColorAlt);
                    System.out.print("quit");
                    System.out.print(textColorDefault);
                    System.out.print(" - close this program \n");
                    break;
                }
                case "register":
                case "Register": {
                    if(checkLength(command, 4)) {
                        String username = command[1];
                        String password = command[2];
                        String email = command[3];
                        Object obj = Client.register(username, password, email);
                        if(obj instanceof Auth auth) {
                            System.out.print(textColorDefault);
                            System.out.print("User successfully created! \n");
                            this.state = "postlogin";
                            this.auth = auth;
                        } else {
                            System.out.print(textColorError);
                            System.out.print("Error creating user: ");
                            System.out.println(obj);
                        }
                    }
                    break;
                }
                case "login":
                case "Login": {
                    if(checkLength(command, 3)) {
                        String username = command[1];
                        String password = command[2];
                        Object obj = Client.login(username, password);
                        if(obj instanceof Auth auth) {
                            System.out.print(textColorDefault);
                            System.out.print("Successfully Logged In! \n");
                            this.state = "postlogin";
                            this.auth = auth;
                        } else {
                            System.out.print(textColorError);
                            System.out.print("Error logging in: ");
                            System.out.println(obj);
                        }
                    }
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

    public Auth getAuth() {
        return this.auth;
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
