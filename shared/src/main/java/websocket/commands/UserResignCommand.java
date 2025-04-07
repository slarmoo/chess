package websocket.commands;

import model.Auth;

public class UserResignCommand extends UserGameCommand {
    public UserResignCommand(Auth auth, int gameID) {
        this.commandType = CommandType.RESIGN;
        this.authToken = auth.authToken();
        this.gameID = gameID;
    }
}
