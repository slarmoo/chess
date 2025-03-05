package model;

import chess.ChessGame;
import com.google.gson.Gson;

public record JoinGameRequest(String playerColor, int gameID) {

    public String toString() {
        return new Gson().toJson(this);
    }

    public ChessGame.TeamColor convertStringToColor() {
        if(playerColor.contains("w") || playerColor.contains("W")) {
            return ChessGame.TeamColor.WHITE;
        } else {
            return  ChessGame.TeamColor.BLACK;
        }
    }
}
