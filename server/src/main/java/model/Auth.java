package model;

import com.google.gson.Gson;

import java.util.Objects;

public record Auth (String username, String authToken) {

    public String toString() {
        return new Gson().toJson(this);
    }

//    @Override
//    public int hashCode() {
//        return username.hashCode() + authToken.hashCode();
//    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Auth(String username1, String token))) {
            return false;
        }
        return /*Objects.equals(username, username1) && */Objects.equals(authToken, token);
    }
}
