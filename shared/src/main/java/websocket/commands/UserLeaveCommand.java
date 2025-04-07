package websocket.commands;

import model.Auth;

public class UserLeaveCommand extends UserGameCommand {
    public UserLeaveCommand(Auth auth, int gameID) {
        this.commandType = CommandType.LEAVE;
        this.authToken = auth.authToken();
        this.gameID = gameID;
    }
}
