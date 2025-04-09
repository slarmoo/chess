package websocket;
import javax.websocket.*;
import java.net.URI;
import java.util.Scanner;

public class WebsocketFacade {
    public Session session;

    public static void main(String[] args) throws Exception {
        var ws = new WebsocketFacade();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a message you want to echo");
        while (true) ws.send(scanner.nextLine());
    }

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
            System.out.print("unable to set up websocket");
        }
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}