import chess.*;
import ui.*;

import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN);
        System.out.println("♕ 240 Chess Client: " + piece);
        var game = new ChessGame();
        var pregame = new PregameUI();
        pregame.start();
        var postlogin = new PostloginUI(pregame.getAuth());
        if(Objects.equals(pregame.getState(), "postlogin")) {
            postlogin.start();
            ChessBoardUI.printBoard(game.getBoard(), false);
        }
    }
}