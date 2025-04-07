package websocket.messages;

public class ServerErrorMessage extends ServerMessage {
    public String errorMessage;

    public ServerErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }
}
