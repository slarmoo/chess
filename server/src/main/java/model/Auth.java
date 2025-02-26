package model;

import com.google.gson.Gson;

public record Auth (String username, String authToken) {

    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public int hashCode() {
        return username.hashCode() + authToken.hashCode();
    }
}
