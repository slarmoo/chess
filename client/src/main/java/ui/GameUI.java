package ui;

import chess.ChessGame;
import model.Game;

import java.util.Objects;
import java.util.Scanner;

public class GameUI extends UI {
    private Game game;
    private ChessGame.TeamColor yourColor;

    public GameUI(Game game, ChessGame.TeamColor yourColor) {
        this.game = game;
        this.yourColor = yourColor;
    }

    public void start(State state) {
        this.state = state;
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
