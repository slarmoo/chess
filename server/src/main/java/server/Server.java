package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.Database;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import spark.*;
import service.*;
import model.*;

import java.util.Collection;
import java.util.Map;

//import java.util.Map;

public class Server {
    private final Database database = new Database();
    private final service.Service service = new service.Service(new MemoryUserDAO(database), new MemoryGameDAO(database));

    public Server() {
//        this.service = service;
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("/web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::createUser);

        Spark.delete("/db", this::deleteAll);

        Spark.post("/game", this::createGame);

        Spark.post("/session", this::loginUser);

        Spark.delete("/session", this::logoutUser);

        Spark.get("/game", this::getGames);

        Spark.put("/game", this::joinGame);

        Spark.exception(exception.ResponseException.class, this::exceptionHandler);


        Spark.awaitInitialization();

        return this.port();
    }

    public int port() {
        return Spark.port();
    }


    public void stop() {
        Spark.stop();
    }

    private void exceptionHandler(exception.ResponseException ex, Request req, Response res) {
        res.status(ex.StatusCode());
        res.body(ex.toJson());
    }

    private Object deleteAll(Request request, Response response) {
        service.delete();
        response.status(200);
        return "";
    }

    private Object createUser(Request request, Response response) throws exception.ResponseException {
        var user = new Gson().fromJson(request.body(), User.class);
        System.out.println(user);
        if(user.username() == null || user.email() == null || user.password() == null) {
            throw new exception.ResponseException(400, "Error: Bad Request");
        }
        try {
            var auth = service.addUser(user);
            response.status(200);
            return new Gson().toJson(auth);
        } catch (DataAccessException e) {
            throw new exception.ResponseException(403, e.getMessage());
        }
    }

    private Object createGame(Request request, Response response) throws exception.ResponseException {
        var a = request.headers("authorization");
        System.out.println("headers: " + request.headers());
        System.out.println("authheader: " + a);
        var auth = new Auth("", a);
        var game = new Gson().fromJson(request.body(), Game.class);
        try {
            game = service.createGame(auth, game.gameName());
            response.status(200);
            return new Gson().toJson(game);
        } catch (DataAccessException e) {
            throw new exception.ResponseException(403, e.getMessage());
        }
    }

    public Object loginUser(Request request, Response response) throws exception.ResponseException {
        var user = new Gson().fromJson(request.body(), User.class);
        System.out.println(user);
        try {
            var auth = service.login(user);
            System.out.println(auth);
            response.status(200);
            return new Gson().toJson(auth);
        } catch (DataAccessException e) {
            throw new exception.ResponseException(401, e.getMessage());
        }
    }

    public Object logoutUser(Request request, Response response) throws exception.ResponseException {
        var a = request.headers("authorization");
        System.out.println("headers: " + request.headers());
        System.out.println("authheader: " + a);
        var auth = new Auth("", a);
        boolean b = service.logout(auth);
        System.out.println(b);
        if(b) {
            response.status(200);
        } else {
            throw new exception.ResponseException(401, "Error: Unauthorized");
        }
        return "";
    }

    public Object getGames(Request request, Response response) throws exception.ResponseException {
        var a = request.headers("authorization");
        System.out.println("headers: " + request.headers());
        System.out.println("authheader: " + a);
        var auth = new Auth("", a);
        try {
            Collection<Game> games = service.getGames(auth);
            response.status(200);
            return new Gson().toJson(Map.of("games", games.toArray()));
        } catch (DataAccessException e) {
            throw new exception.ResponseException(401, e.getMessage());
        }
    }

    public Object joinGame(Request request, Response response) throws exception.ResponseException {
        var user = new Gson().fromJson(request.body(), User.class);
        try {
            var auth = service.login(user);
            response.status(200);
            return new Gson().toJson(auth);
        } catch (DataAccessException e) {
            throw new exception.ResponseException(401, e.getMessage());
        }
    }
}