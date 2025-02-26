package server;

import com.google.gson.Gson;
import dataaccess.MemoryUserDAO;
import spark.*;
import service.*;
import model.*;

//import java.util.Map;

public class Server {

    private final service.Service service = new UserService(new MemoryUserDAO());

    public Server() {
//        this.service = service;
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("/web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", (request, response) -> {
            var user = new Gson().fromJson(request.body(), User.class);
            var auth = service.addUser(user);
            response.body(new Gson().toJson(auth));
            return 200;
        });

        Spark.delete("/db", (request, response) -> {
            return 200;
        });

        Spark.awaitInitialization();

        return this.port();
    }

    public int port() {
        return Spark.port();
    }


    public void stop() {
        Spark.stop();
    }
}