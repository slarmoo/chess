package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.Database;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import org.eclipse.jetty.client.HttpResponseException;
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

        Spark.post("/game", (request, response) -> {
            var auth = new Gson().fromJson(request.headers().toString(), Auth.class);
            var game = new Gson().fromJson(request.body(), Game.class);
            game = service.createGame(auth, game.gameName());
            response.body(new Gson().toJson(game));
            return 200;
        });
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

    private Object deleteAll(Request request, Response response) throws exception.ResponseException {
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
//            System.out.println(e);
//            response.body(new Gson().toJson(e.toString()));
//            response.status(403);
//            return e.toString();
//        }
        return "";
    }
}