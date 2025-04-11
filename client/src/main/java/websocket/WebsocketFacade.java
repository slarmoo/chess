package websocket;
import javax.websocket.*;
import java.net.URI;
import com.google.gson.Gson;

public class WebsocketFacade extends Endpoint {
    public Session session;

    public WebsocketFacade(int port) {
        try {
            URI uri = new URI("ws://localhost:" + port + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                public void onMessage(String message) {
                    System.out.println(message);
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