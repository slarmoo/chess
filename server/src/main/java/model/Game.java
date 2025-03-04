package model;

import chess.ChessGame;
import com.google.gson.Gson;

public record Game(int ID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public int hashCode() {
        return ID;
    }
}
