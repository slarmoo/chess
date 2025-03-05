package model;

import chess.ChessGame;
import com.google.gson.Gson;

public record JoinGameRequest(String playerColor, int gameID) {

    public String toString() {
        return new Gson().toJson(this);
    }

    public ChessGame.TeamColor convertStringToColor() {
        if(playerColor != null) {
            if (playerColor.toLowerCase().contains("white")) {
                return ChessGame.TeamColor.WHITE;
            } else if (playerColor.toLowerCase().contains("black")) {
                return ChessGame.TeamColor.BLACK;
            }
        }
        return null;
    }
}
