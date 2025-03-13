package model;

import chess.ChessGame;
import com.google.gson.Gson;

import java.util.Objects;

public record Game(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Game(int gameID1, String whiteUsername1, String blackUsername1, String gameName1, ChessGame game1))) {
            return false;
        }
        if(whiteUsername1 != null && !Objects.equals(whiteUsername, whiteUsername1)) {
            return false;
        }
        if(blackUsername1 != null && !Objects.equals(blackUsername, blackUsername1)) {
            return false;
        }
        return Objects.equals(gameID, gameID1) && Objects.equals(gameName, gameName1) && game.equals(game1);
    }

    @Override
    public int hashCode() {
        return gameID;
    }
}

