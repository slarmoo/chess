package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import model.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

@WebSocket
public class WSServer {
    private final service.Service service;
    private final ConnectionManager connectionManager = new ConnectionManager();

    public WSServer(service.Service service) {
        this.service = service;
    }

    @OnWebSocketError
    public void onError(java.lang.Throwable e) {
        try {
            if (e instanceof WebsocketException websocketException) {
                connectionManager.send(websocketException.username, new ServerErrorMessage(websocketException.getMessage()));
            } else {
                System.out.println("Error: " + e + " " + e.getMessage());
            }
        } catch(Exception ex) {
            System.out.println("Error: " + e + " " + e.getMessage());
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        Auth auth = new Auth("", command.getAuthToken());
        String username = service.getUsernameFromAuth(auth);
        System.out.println("here");
        if(service.validateAuth(new Auth(username, command.getAuthToken()))) {
            switch (command.getCommandType()) {
                case CONNECT -> connect(new Gson().fromJson(message, UserConnectCommand.class), session, username, auth);
                case MAKE_MOVE -> makeMove(new Gson().fromJson(message, UserMakeMoveCommand.class), username, auth);
                case LEAVE -> leave(new Gson().fromJson(message, UserLeaveCommand.class), username);
                case RESIGN -> resign(new Gson().fromJson(message, UserResignCommand.class), username, auth);
            }
        } else {
            System.out.println("sending error (bad auth)");
            session.getRemote().sendString(new Gson().toJson(new ServerErrorMessage("Error: Unable to Validate User")));
        }
    }

    private void connect(UserConnectCommand connectCommand, Session session, String username, Auth auth) throws Exception {
        System.out.println(session.toString());
        connectionManager.add(username, session, connectCommand.getGameID());
        Game game = this.getGame(connectCommand.getGameID(), auth);
        if(game == null) {
            throw new WebsocketException("Error: Game of ID " + connectCommand.getGameID() + " does not exist", username);
        }
        connectionManager.send(username, new ServerLoadGameMessage(game.game()));
        connectionManager.broadcast(username, new ServerNotificationMessage(username + " joined the game"), connectCommand.getGameID());
    }

    private void makeMove(UserMakeMoveCommand makeMoveCommand, String username, Auth auth) throws Exception {
        Game game = this.getGame(makeMoveCommand.getGameID(), auth);
        if(game == null) {
            throw new WebsocketException("Error: Game of ID " + makeMoveCommand.getGameID() + " does not exist", username);
        }
        if(game.game().isGameOver) {
            throw new WebsocketException("Error: Game of ID " + makeMoveCommand.getGameID() + " is over", username);
        }
        ChessGame.TeamColor yourColor = (Objects.equals(username, game.whiteUsername()) ?
                ChessGame.TeamColor.WHITE : Objects.equals(username, game.blackUsername()) ?
                ChessGame.TeamColor.BLACK : null);
        if(makeMoveCommand.move != null && game.game().getTeamTurn().equals(yourColor) &&
        game.game().validMoves(makeMoveCommand.move.getStartPosition()).contains(makeMoveCommand.move) &&
        game.game().getBoard().getPiece(makeMoveCommand.move.getStartPosition()).getTeamColor() == yourColor) {
            try {
                game.game().makeMove(makeMoveCommand.move);
                String opponentUsername = Objects.equals(username, game.whiteUsername()) ?
                        game.blackUsername() : game.whiteUsername();
                ChessGame.TeamColor opponentColor = (Objects.equals(username, game.whiteUsername()) ?
                        ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE);
                if(game.game().isInCheckmate(opponentColor)) {
                    game.game().isGameOver = true;
                    connectionManager.broadcast("",
                            new ServerNotificationMessage(opponentUsername + " is in checkmate\n " + username + " wins!"), makeMoveCommand.getGameID());
                } else if(game.game().isInCheck(opponentColor)) {
                    connectionManager.broadcast("", new ServerNotificationMessage(opponentUsername + " is in check"), makeMoveCommand.getGameID());
                } else if(game.game().isInStalemate(opponentColor)) {
                    game.game().isGameOver = true;
                    connectionManager.broadcast("",
                            new ServerNotificationMessage(opponentUsername + " is in stalemate\n" + "It's a draw"), makeMoveCommand.getGameID());
                }
                service.updateGame(game.game(), makeMoveCommand.getGameID());
                connectionManager.broadcast("", new ServerLoadGameMessage(game.game()), makeMoveCommand.getGameID());
                connectionManager.broadcast(username, new ServerNotificationMessage(username + " made move " + makeMoveCommand.move), makeMoveCommand.getGameID());
            } catch(InvalidMoveException e) {
                connectionManager.send(username, new ServerErrorMessage(e.getMessage()));
            }
        } else {
            throw new WebsocketException("Error: Invalid Move", username);
        }

    }

    private void leave(UserLeaveCommand leaveCommand, String username) throws Exception {
        connectionManager.remove(username);
        connectionManager.broadcast(username, new ServerNotificationMessage(username + " left the game"), leaveCommand.getGameID());
    }

    private void resign(UserResignCommand resignCommand, String username, Auth auth) throws Exception {
        Game game = getGame(resignCommand.getGameID(), auth);
        if(game == null) {
            throw new WebsocketException("Error: Game of ID " + resignCommand.getGameID() + " does not exist", username);
        }
        if(game.game().isGameOver) {
            throw new WebsocketException("Error: Game of ID " + resignCommand.getGameID() + " is over", username);
        }
        ChessGame.TeamColor yourColor = (Objects.equals(username, game.whiteUsername()) ?
                ChessGame.TeamColor.WHITE : Objects.equals(username, game.blackUsername()) ?
                ChessGame.TeamColor.BLACK : null);
        if(yourColor != null) {
            game.game().isGameOver = true;
            service.updateGame(game.game(), resignCommand.getGameID());
            connectionManager.broadcast("",
                    new ServerNotificationMessage(username + " resigned\n it's a draw"), resignCommand.getGameID());
        } else {
            throw new WebsocketException(username, "Error: cannot resign as a spectator. Try leaving instead");
        }
    }

    private Game getGame(int id, Auth auth) throws Exception {
        Collection<Game> games = service.getGames(auth);
        for(Game g : games) {
            if(g.gameID() ==  id) {
                return g;
            }
        }
        return null;
    }
}