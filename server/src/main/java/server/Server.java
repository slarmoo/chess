package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.Database;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import spark.*;
import service.*;
import model.*;

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
        try {
            var auth = service.addUser(user);
            response.body(new Gson().toJson(auth));
            response.status(200);
        } catch (DataAccessException e) {
            throw new exception.ResponseException(403, e.getMessage());
        }
        return "";
    }

    private Object createGame(Request request, Response response) throws exception.ResponseException {
        System.out.println(request.headers().toString() + " " + request.body());
        var auth = new Gson().fromJson(request.headers().toString(), Auth.class);
        var game = new Gson().fromJson(request.body(), Game.class);
        try {
            game = service.createGame(auth, game.gameName());
            response.body(new Gson().toJson(game));
            response.status(200);
        } catch (DataAccessException e) {
            throw new exception.ResponseException(403, e.getMessage());
        }
        return "";
    }

    public Object loginUser(Request request, Response response) throws exception.ResponseException {
        var user = new Gson().fromJson(request.body(), User.class);
        try {
            var auth = service.login(user);
            response.body(new Gson().toJson(auth));
            response.status(200);
        } catch (DataAccessException e) {
            throw new exception.ResponseException(401, e.getMessage());
        }
        return "";
    }

    public Object logoutUser(Request request, Response response) {
        System.out.println(request.headers().toString() + " " + request.headers().toArray()[0]);
        var auth = new Gson().fromJson(request.headers().toString(), Auth.class);
        service.logout(auth);
        response.status(200);
        return "";
    }

    public Object getGames(Request request, Response response) throws exception.ResponseException {
        var user = new Gson().fromJson(request.body(), User.class);
        try {
            var auth = service.login(user);
            response.body(new Gson().toJson(auth));
            response.status(200);
        } catch (DataAccessException e) {
            throw new exception.ResponseException(401, e.getMessage());
        }
        return "";
    }

    public Object joinGame(Request request, Response response) throws exception.ResponseException {
        var user = new Gson().fromJson(request.body(), User.class);
        try {
            var auth = service.login(user);
            response.body(new Gson().toJson(auth));
            response.status(200);
        } catch (DataAccessException e) {
            throw new exception.ResponseException(401, e.getMessage());
        }
        return "";
    }
}