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
        String state = "pregame";
        pregame.start(state);
        state = pregame.getState();
        while(!Objects.equals(state, "game") && !Objects.equals(state, "stop")) {
            switch (state) {
                case "postlogin": {
                    var postlogin = new PostloginUI(pregame.getAuth());
                    postlogin.start(state);
                    state = postlogin.getState();
                    break;
                }
                case "pregame": {
                    pregame.start(state);
                    state = pregame.getState();
                    break;
                }
            }
//            System.out.println(state);
        }
        ChessBoardUI.printBoard(game.getBoard(), false);
    }
}