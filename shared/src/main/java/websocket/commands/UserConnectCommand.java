package websocket.commands;

import model.Auth;

public class UserConnectCommand extends UserGameCommand{
    public UserConnectCommand(Auth auth, int gameID) {
        this.commandType = CommandType.CONNECT;
        this.authToken = auth.authToken();
        this.gameID = gameID;
    }
}
