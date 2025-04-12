package server.websocket;

public class WebsocketException extends RuntimeException {
    public String username;

    public WebsocketException(String message, String username) {
        super(message);
        this.username = username;
    }
}
