package websocket.commands;

import chess.ChessMove;
import model.Auth;

public class UserMakeMoveCommand extends UserGameCommand {
    public ChessMove move;

    public UserMakeMoveCommand(Auth auth, int gameID, ChessMove move) {
        this.commandType = CommandType.MAKE_MOVE;
        this.authToken = auth.authToken();
        this.gameID = gameID;
        this.move = move;
    }
}
