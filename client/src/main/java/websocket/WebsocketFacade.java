package websocket;
import javax.websocket.*;
import java.net.URI;

import chess.ChessGame;
import com.google.gson.Gson;
import ui.ChessBoardUI;
import ui.EscapeSequences;
import ui.WebsocketUI;
import websocket.messages.ServerErrorMessage;
import websocket.messages.ServerLoadGameMessage;
import websocket.messages.ServerMessage;
import websocket.messages.ServerNotificationMessage;

public class WebsocketFacade extends Endpoint {
    public Session session;

    public WebsocketUI websocketUI = new WebsocketUI();

    public WebsocketFacade(int port) {
        try {
            URI uri = new URI("ws://localhost:" + port + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                public void onMessage(String message) {
                    var msg = new Gson().fromJson(message, ServerMessage.class);
                    switch (msg.getServerMessageType()) {
                        case NOTIFICATION: {
                            var notif = new Gson().fromJson(message, ServerNotificationMessage.class);
                            websocketUI.notify(notif);
                            break;
                        }
                        case ERROR: {
                            var error = new Gson().fromJson(message, ServerErrorMessage.class);
                            websocketUI.error(error);
                            break;
                        }
                        case LOAD_GAME: {
                            var gameMessage = new Gson().fromJson(message, ServerLoadGameMessage.class);
                            websocketUI.loadGame(gameMessage);
                            break;
                        }
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("unable to set up websocket");
        }
    }

    public void send(Object msg) throws Exception {
        this.session.getBasicRemote().sendText(new Gson().toJson(msg));
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}