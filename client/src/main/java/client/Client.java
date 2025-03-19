package client;

import com.google.gson.Gson;
import model.*;

import java.io.*;
import java.net.*;

public class Client {
    private static final String urlBase = "http://localhost:8081/";

    public static Object register(String username, String password, String email) {
        try {
            URI uri = new URI(urlBase + "user");
            HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
            http.setRequestMethod("POST");

            // Specify that we are going to write out data
            http.setDoOutput(true);

            // Write out a header
            http.addRequestProperty("Content-Type", "application/json");

            // Write out the body
            var outputStream = http.getOutputStream();
            var jsonBody = new Gson().toJson(new User(username, password, email));
            outputStream.write(jsonBody.getBytes());

            // Make the request
            http.connect();

            var status = http.getResponseCode();
            InputStream in = http.getInputStream();
            if ( status >= 200 && status < 300) {
                return new Gson().fromJson(new InputStreamReader(in), Auth.class);
            } else {
                return new Gson().fromJson(new InputStreamReader(in), Object.class);
            }

        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
