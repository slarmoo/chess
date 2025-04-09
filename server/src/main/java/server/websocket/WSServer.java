package server.websocket;

import com.google.gson.Gson;
import model.Auth;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;

@WebSocket
public class WSServer {
    private final service.Service service;
    private final ConnectionManager connectionManager = new ConnectionManager();

    public WSServer(service.Service service) {
        this.service = service;
    }

    @OnWebSocketError
    public void onError(java.lang.Throwable e) {
        System.out.println("Error: " + e + " " + e.getMessage());
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        System.out.println("here");
        if(service.validateAuth(new Auth(command.getUsername(), command.getAuthToken()))) {
            switch (command.getCommandType()) {
                case CONNECT -> connect(command, session);
                case MAKE_MOVE -> makeMove(command);
                case LEAVE -> leave(command);
                case RESIGN -> resign(command);
            }
        } else {
            connectionManager.send(command.getUsername(), new ServerErrorMessage("Unable to Validate User"));
        }
    }

    private void connect(UserGameCommand connectCommand, Session session) throws IOException {
        System.out.println("here2");
        connectionManager.add(connectCommand.getUsername(), session);
        connectionManager.broadcast(connectCommand.getUsername(), new ServerNotificationMessage(connectCommand.getUsername() + " joined the game"));
    }

    private void makeMove(UserGameCommand makeMoveCommand) {

    }

    private void leave(UserGameCommand leaveCommand) {

    }

    private void resign(UserGameCommand resignCommand) {

    }
}