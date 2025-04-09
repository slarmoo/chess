package websocket.commands;

import model.Auth;

public class UserConnectCommand extends UserGameCommand{
    public UserConnectCommand(Auth auth, int gameID) {
        super(CommandType.CONNECT, auth.authToken(), gameID, auth.username());
    }
}
