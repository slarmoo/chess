package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import model.Auth;
import model.Game;

import java.text.ParseException;
import java.util.Objects;
import java.util.Scanner;

public class GameUI extends UI {
    private Game game;
    private ChessGame.TeamColor yourColor;

    public GameUI(Auth auth, Game game, ChessGame.TeamColor yourColor) {
        this.game = game;
        this.yourColor = yourColor;
        this.auth = auth;
    }

    public void start(State state) {
        this.state = state;
        this.renderBoard();
        while (Objects.equals(this.state, State.game)) {
            System.out.print(TEXT_COLOR_DEFAULT);
            System.out.print("Type Help to get started>>> ");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var command = line.split(" ");

            switch(command[0]) {
                case "help":
                case "Help": {
                    GameUI.printHelp();
                    break;
                }
                case "redraw":
                case "Redraw":
                    this.renderBoard();
                    break;
                case "move":
                case "Move":
                    if(this.checkLength(command, 3)) {
                        try {
                            ChessMove move = parseMove(command[1], command[2]);
                        } catch (Exception e) {
                            System.out.print(TEXT_COLOR_ERROR);
                            System.out.println("Incorrect Syntax. Use letter-number (\"A1\") to denote a position");
                        }
                        this.renderBoard();
                    } else {
                        System.out.print(TEXT_COLOR_ERROR);
                        System.out.println("Not enough arguments");
                    }
                    break;
                case "resign":
                case "Resign":
                    break;
                case "leave":
                case "Leave":
                    serverFacade.leaveGame(auth, game.gameID());
                    this.state = State.postlogin;
                    break;
                case null, default: {
                    System.out.print(TEXT_COLOR_ERROR);
                    System.out.println("Unrecognized command. Type Help for help");
                    break;
                }
            }
        }
    }

    private void renderBoard() {
        boolean isRightSideUp = Objects.equals(this.yourColor, ChessGame.TeamColor.WHITE);
        ChessBoardUI.printBoard(game.game().getBoard(), isRightSideUp);
    }

    private ChessMove parseMove(String start, String end) throws Exception {
        return new ChessMove(parsePosition(start), parsePosition(end), null);
    }

    private ChessPosition parsePosition(String pos) throws Exception {
        char letter = pos.charAt(0);
        int number = Integer.parseInt(pos.charAt(1) + "");
        System.out.print(letter);
        return new ChessPosition(number, letter);
    }

    private static void printHelp() {

        System.out.print(TEXT_COLOR_ALT);
        System.out.print("redraw");
        System.out.print(TEXT_COLOR_DEFAULT);
        System.out.print(" - redraw the chess board \n");

        System.out.print(TEXT_COLOR_ALT);
        System.out.print("leave");
        System.out.print(TEXT_COLOR_DEFAULT);
        System.out.print(" - leave game \n");

        System.out.print(TEXT_COLOR_ALT);
        System.out.print("move <Position 1> <Position 2>");
        System.out.print(TEXT_COLOR_DEFAULT);
        System.out.print(" - move a chess piece \n");

        System.out.print(TEXT_COLOR_ALT);
        System.out.print("resign");
        System.out.print(TEXT_COLOR_DEFAULT);
        System.out.print(" - resign from chess game \n");

        System.out.print(TEXT_COLOR_ALT);
        System.out.print("highlight <Position>");
        System.out.print(TEXT_COLOR_DEFAULT);
        System.out.print(" - shows all legal moves that a chess piece can make \n");

        System.out.print(TEXT_COLOR_ALT);
        System.out.print("help");
        System.out.print(TEXT_COLOR_DEFAULT);
        System.out.print(" - help with what commands you can use \n");
    }
}
