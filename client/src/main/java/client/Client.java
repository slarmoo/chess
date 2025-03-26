package client;

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
            return writeObjectToPath(gameName,"game", "POST", Game.class, auth);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private static Object writeObjectToPath(Object obj, String path, String requestMethod, Class classType, Auth auth) throws Exception {
        URI uri = new URI(urlBase + path);

        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
//        System.out.println(requestMethod);
        http.setRequestMethod(requestMethod);

        // Specify that we are going to write out data
        http.setDoOutput(true);

        // Write out a header
        http.addRequestProperty("Content-Type", "application/json");
        if(auth != null) {
//            System.out.println("writing auth");
            http.addRequestProperty("authorization", auth.authToken());
//            System.out.println(auth);
        }

        // Write out the body
        var outputStream = http.getOutputStream();
        if(obj != null) {
//            System.out.println("writing object");
//            System.out.println(obj);
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
}
