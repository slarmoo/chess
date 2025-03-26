package client;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;

import java.io.*;
import java.net.*;

public class Client {
    private static final String urlBase = "http://localhost:8081/";

    public static Object register(String username, String password, String email) {
        try {
            return writeObjectToPath(new User(username, password, email), "user", "POST", Auth.class, null);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static Object login(String username, String password) {
        try {
            return writeObjectToPath(new User(username, password, null), "session", "POST", Auth.class, null);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static Object logout(Auth auth) {
        try {
            return writeObjectToPath(null, "session", "DELETE", Object.class, auth);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static Object createGame(String gameName, Auth auth) {
        try {
            return writeObjectToPath(new Game(0, "", "", gameName, new ChessGame()),
                    "game", "POST", Game.class, auth);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static Object getGames(Auth auth) {
        try {
            var games = grabGames(auth);
            if(games != null) {
                var result = new StringBuilder();
                for (Game game : games) {
                    result.append(Client.parseGame(game)).append('\n');
                }
                return result;
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return "[none]";
    }

    public static Object joinGame(int id, ChessGame.TeamColor color, Auth auth) {
        try {
            writeObjectToPath(new JoinGameRequest(color.name(), id),
                    "game", "PUT", Auth.class, auth);
            return grabGameWithID(id, auth);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private static Game grabGameWithID(int id, Auth auth) throws Exception {
        var games = grabGames(auth);
        if(games != null) {
            for (Game game : games) {
                if(game.gameID() == id) {
                    return game;
                }
            }
        }
        return null;
    }

    private static <T> Object writeObjectToPath(Object obj, String path, String requestMethod, Class<T> classType, Auth auth) throws Exception {
        URI uri = new URI(urlBase + path);

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

    private static Game[] grabGames(Auth auth) throws Exception {
        record listGamesResponse(Game[] games) {
        }
        URI uri = new URI(urlBase + "game");

        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("GET");
        http.addRequestProperty("Content-Type", "application/json");
        if(auth != null) {
            http.addRequestProperty("authorization", auth.authToken());
        }
        http.connect();
        InputStream in = http.getInputStream();
        var response = new Gson().fromJson(new InputStreamReader(in), listGamesResponse.class);
        if(response instanceof listGamesResponse gamesResponse) {
            return gamesResponse.games();
        } else {
            return null;
        }
    }

    private static String parseGame(Game game) {
        var result = new StringBuilder();
        var name = game.gameName();
        var id = game.gameID();
        var blackPlayer = game.blackUsername();
        var whitePlayer = game.whiteUsername();
        result.append("Game: ").append(name);
        result.append(" \t| ID: ").append(id);
        result.append(" \t| White: ").append(whitePlayer == null ? "open" : whitePlayer);
        result.append(" \t| Black: ").append(blackPlayer == null ? "open" : blackPlayer);
        return result.toString();
    }
}
