package model;

import com.google.gson.Gson;

public record Game(int ID, String whiteUsername, String blackUsername, String gameName) {
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public int hashCode() {
        return ID;
    }
}
