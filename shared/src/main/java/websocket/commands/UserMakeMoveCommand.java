package websocket.commands;

import chess.ChessMove;
import model.Auth;

public class UserMakeMoveCommand extends UserGameCommand {
    public ChessMove move;

    public UserMakeMoveCommand(Auth auth, int gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, auth.authToken(), gameID);
        this.move = move;
    }
}
