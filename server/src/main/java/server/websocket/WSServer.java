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
        String username = service.getUsernameFromAuth(new Auth("", command.getAuthToken()));
        if(service.validateAuth(new Auth(username, command.getAuthToken()))) {
            switch (command.getCommandType()) {
                case CONNECT -> connect(command, session, username);
                case MAKE_MOVE -> makeMove(command, username);
                case LEAVE -> leave(command, username);
                case RESIGN -> resign(command, username);
            }
        } else {
            connectionManager.send(username, new ServerErrorMessage("Unable to Validate User"));
        }
    }

    private void connect(UserGameCommand connectCommand, Session session, String username) throws Exception {
        connectionManager.add(username, session);
        connectionManager.broadcast(username, new ServerNotificationMessage(username + " joined the game"));
    }

    private void makeMove(UserGameCommand makeMoveCommand, String username) {

    }

    private void leave(UserGameCommand leaveCommand, String username) {

    }

    private void resign(UserGameCommand resignCommand, String username) {

    }
}