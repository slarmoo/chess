package websocket.commands;

import model.Auth;

public class UserResignCommand extends UserGameCommand {
    public UserResignCommand(Auth auth, int gameID) {
        super(CommandType.RESIGN, auth.authToken(), gameID, auth.username());
    }
}
