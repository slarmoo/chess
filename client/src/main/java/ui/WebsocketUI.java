package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import model.Game;
import websocket.messages.ServerErrorMessage;
import websocket.messages.ServerLoadGameMessage;
import websocket.messages.ServerNotificationMessage;

import java.util.Objects;

public class WebsocketUI {
    private static final String TEXT_COLOR_DEFAULT = EscapeSequences.SET_TEXT_COLOR_GREEN;
    private static final String TEXT_COLOR_WEBSOCKET = EscapeSequences.SET_TEXT_COLOR_BLUE;
    private static final String TEXT_COLOR_ERROR = EscapeSequences.SET_TEXT_COLOR_RED;

    private Game game;
    private ChessGame.TeamColor yourColor;

    public void setUp(Game game, ChessGame.TeamColor color) {
        this.game = game;
        this.yourColor = color;
    }

    private void updateGame(ChessGame chessGame) {
        this.game = new Game(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), chessGame);
    }

    public void notify(ServerNotificationMessage notification) {
        System.out.print(TEXT_COLOR_WEBSOCKET);
        System.out.println("\n Websocket: " + notification.message);
        System.out.print(TEXT_COLOR_DEFAULT);
    }

    public void error(ServerErrorMessage error) {
        System.out.println(TEXT_COLOR_ERROR);
        System.out.println("Websocket: " + error.errorMessage);
        System.out.print(TEXT_COLOR_DEFAULT);
    }

    public void loadGame(ServerLoadGameMessage loadGame) {
        this.updateGame(loadGame.game);
        this.renderBoard();
    }

    private void renderBoard() {
        boolean isRightSideUp = Objects.equals(this.yourColor, ChessGame.TeamColor.WHITE);
        ChessBoardUI.printBoard(game.game().getBoard(), isRightSideUp);
    }
}
