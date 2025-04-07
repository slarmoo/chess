package websocket.messages;

import chess.ChessGame;

public class ServerLoadGameMessage extends ServerMessage {
    public ChessGame game;

    public ServerLoadGameMessage(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }
}
