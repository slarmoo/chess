import chess.*;
import client.ServerFacade;
import model.Game;
import ui.*;

import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN);
        System.out.println("♕ 240 Chess Client: " + piece);
        ServerFacade server = new ServerFacade(8080);
        var pregame = new PregameUI(server);
        State state = State.pregame;
        Game game = null;
        pregame.start(state);
        var postlogin = new PostloginUI(pregame.getAuth(), server);
        state = pregame.getState();
        while(!Objects.equals(state, State.stop)) {
            switch (state) {
                case State.postlogin: {
                    postlogin.start(state);
                    state = postlogin.getState();
                    game = postlogin.getGame();
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
                    if(game != null) {
                        var gameUI = new GameUI(postlogin.getAuth(), game, postlogin.getColor(), server);
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