import chess.*;
import model.Game;
import ui.*;

import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN);
        System.out.println("♕ 240 Chess Client: " + piece);
        var pregame = new PregameUI();
        State state = State.pregame;
        pregame.start(state);
        var postlogin = new PostloginUI(pregame.getAuth());
        state = pregame.getState();
        while(!Objects.equals(state, State.stop)) {
            switch (state) {
                case State.postlogin: {
                    postlogin.start(state);
                    state = postlogin.getState();
                    break;
                }
                case State.pregame: {
                    pregame.start(state);
                    state = pregame.getState();
                    postlogin.setAuth(pregame.getAuth());
                    break;
                }
                case State.game: {
                    boolean isRightSideUp = true;
                    if(postlogin.getGame() != null) {
                        Game game = postlogin.getGame();
                        var gameUI = new GameUI(postlogin.getAuth(), postlogin.getGame(), postlogin.getColor());
                        gameUI.start(state);
                        state = gameUI.getState();
                        break;
                    }
                    state = State.postlogin;
                    break;
                }
            }
        }
    }
}