package client;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import model.*;
import websocket.WebsocketFacade;
import websocket.commands.*;

import java.io.*;
import java.net.*;

public class ServerFacade {
    private final String serverUrl;
    public final WebsocketFacade websocket;

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port + "/";
        websocket = new WebsocketFacade(port);
    }

    public Object register(String username, String password, String email) {
        try {
            return writeObjectToPath(new User(username, password, email), "user", "POST", Auth.class, null);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public Object login(String username, String password) {
        try {
            return writeObjectToPath(new User(username, password, null), "session", "POST", Auth.class, null);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public Object logout(Auth auth) {
        try {
            return writeObjectToPath(null, "session", "DELETE", Object.class, auth);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public Object createGame(String gameName, Auth auth) {
        try {
            return writeObjectToPath(new Game(0, "", "", gameName, new ChessGame()),
                    "game", "POST", Game.class, auth);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String getGames(Auth auth) {
        try {
            var games = grabGames(auth);
            if(games != null && games.length != 0) {
                var result = new StringBuilder();
                int count = 1;
                for (Game game : games) {
                    result.append(ServerFacade.parseGame(game, count)).append('\n');
                    count++;
                }
                return result.toString();
            }
        } catch (Exception e) {
            return null;
        }
        return "[empty] \n";
    }

    public Object joinGame(int id, ChessGame.TeamColor color, Auth auth) {
        try {
            Game game = grabGameWithID(id, auth);
            writeObjectToPath(new JoinGameRequest(color.name(), game.gameID()),
                    "game", "PUT", Auth.class, auth);
            websocket.send(new UserConnectCommand(auth, game.gameID()));
            return grabGameWithID(id, auth); //grab updated version
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public void spectateGame(Auth auth, int gameID) {
        try {
            websocket.send(new UserConnectCommand(auth, gameID));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void makeMove(Auth auth, int gameID, ChessMove move) {
        try {
            websocket.send(new UserMakeMoveCommand(auth, gameID, move));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void leaveGame(Auth auth, int gameID) {
        try {
            websocket.send(new UserLeaveCommand(auth, gameID));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void resignGame(Auth auth, int gameID) {
        try {
            websocket.send(new UserResignCommand(auth, gameID));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void delete() {
        try {
            writeObjectToPath(null, "db", "DELETE", null, null);
        } catch(Exception e) {
            System.out.println("unable to delete");
        }
    }

    public Game grabGameWithID(int id, Auth auth) throws Exception {
        var games = grabGames(auth);
        if(games != null) {
            int count = 1;
            for (Game game : games) {
                if(count == id) {
                    return game;
                }
                count++;
            }
        }
        return null;
    }

    public void emptyDatabase() {
        try {
            writeObjectToPath(null, "db", "DELETE", Object.class, null);
        } catch (Exception e) {
            return;
        }
    }

    private <T> Object writeObjectToPath(Object obj, String path, String requestMethod, Class<T> classType, Auth auth) throws Exception {
        URI uri = new URI(serverUrl + path);

        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod(requestMethod);

        // Specify that we are going to write out data
        http.setDoOutput(true);

        // Write out a header
        http.addRequestProperty("Content-Type", "application/json");
        if(auth != null) {
            http.addRequestProperty("authorization", auth.authToken());
        }

        // Write out the body
        var outputStream = http.getOutputStream();
        if(obj != null) {
            outputStream.write(new Gson().toJson(obj).getBytes());
        }

//         Make the request
            http.connect();

        var status = http.getResponseCode();
        InputStream in = http.getInputStream();

        Object returnObj;
        if ( status >= 200 && status < 300) {
            returnObj =  new Gson().fromJson(new InputStreamReader(in), classType); //okay, create proper output
        } else {
            returnObj = new Gson().fromJson(new InputStreamReader(in), String.class); //otherwise return a default string with error message
        }

        http.disconnect();


        return returnObj;
    }

    private Game[] grabGames(Auth auth) throws Exception {
        record ListGamesResponse(Game[] games) {
        }
        URI uri = new URI(serverUrl + "game");

        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("GET");
        http.addRequestProperty("Content-Type", "application/json");
        if(auth != null) {
            http.addRequestProperty("authorization", auth.authToken());
        }
        http.connect();
        InputStream in = http.getInputStream();
        var response = new Gson().fromJson(new InputStreamReader(in), ListGamesResponse.class);
        if(response instanceof ListGamesResponse gamesResponse) {
            return gamesResponse.games();
        } else {
            return null;
        }
    }

    public static String parseGame(Game game, int fakeID) {
        var result = new StringBuilder();
        var name = game.gameName();
        var id = fakeID;
        var blackPlayer = game.blackUsername();
        var whitePlayer = game.whiteUsername();
        result.append("Game: ").append(name);
        result.append(" \t| ID: ").append(id);
        result.append(" \t| White: ").append(whitePlayer == null ? "open" : whitePlayer);
        result.append(" \t| Black: ").append(blackPlayer == null ? "open" : blackPlayer);
        return result.toString();
    }
}
