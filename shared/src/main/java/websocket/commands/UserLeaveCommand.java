package websocket.commands;

import model.Auth;

public class UserLeaveCommand extends UserGameCommand {
    public UserLeaveCommand(Auth auth, int gameID) {
        super(CommandType.LEAVE, auth.authToken(), gameID, auth.username());
    }
}
