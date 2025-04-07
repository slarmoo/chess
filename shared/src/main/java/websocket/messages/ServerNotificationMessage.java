package websocket.messages;

public class ServerNotificationMessage extends ServerMessage {
    public String message;

    public ServerNotificationMessage(String message) {
        super(ServerMessage.ServerMessageType.NOTIFICATION);
        this.message = message;
    }
}
