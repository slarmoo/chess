package client;

import com.google.gson.Gson;
import model.*;

import java.io.*;
import java.net.*;

public class Client {
    private static final String urlBase = "http://localhost:8081/";

    public static Object register(String username, String password, String email) {
        try {
            return writeObjectToPath(new User(username, password, email), "user", "POST", Auth.class);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static Object login(String username, String password) {
        try {
            return writeObjectToPath(new User(username, password, null), "session", "POST", Auth.class);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static Object logout(Auth auth) {
        try {
            return writeObjectToPath(auth, "session", "DELETE", Object.class);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private static Object writeObjectToPath(Object obj, String path, String requestMethod, Class classType) throws Exception {
        URI uri = new URI(urlBase + path);
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod(requestMethod);

        // Specify that we are going to write out data
        http.setDoOutput(true);

        // Write out a header
        http.addRequestProperty("Content-Type", "application/json");

        // Write out the body
        var outputStream = http.getOutputStream();
        var jsonBody = new Gson().toJson(obj);
        outputStream.write(jsonBody.getBytes());

        // Make the request
        http.connect();

        var status = http.getResponseCode();
        InputStream in = http.getInputStream();
        if ( status >= 200 && status < 300) {
            return new Gson().fromJson(new InputStreamReader(in), classType);
        } else {
            return new Gson().fromJson(new InputStreamReader(in), Object.class);
        }
    }
}
