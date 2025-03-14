package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.Collection;

public class MemoryGameDAO extends SQLDAO implements GameDAO {
    private int id = 0;

    @Override
    public Game addGame(Auth auth, String name) throws DataAccessException {
        if(this.validateAuthSQL(auth)) {
            Game game = this.createGame(name);
            this.addGameSQL(game);
            return game;
        } else {
            throw new DataAccessException("Error: Unauthorized");
        }
    }

    @Override
    public void deleteAll() throws DataAccessException {
        this.deleteAllSQL();
    }

    @Override
    public Collection<Game> findAll() throws DataAccessException {
        return this.getAllGamesSQL();
    }

    @Override
    public void joinGame(Auth auth, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException {
        if(this.validateAuthSQL(auth)) {
            Game game = this.getGameByIDSQL(gameID);
            if(game != null) {
                if((playerColor == ChessGame.TeamColor.WHITE && game.whiteUsername() != null) ||
                    (playerColor == ChessGame.TeamColor.BLACK && game.blackUsername() != null)) {
                    throw new DataAccessException("Error: Forbidden");
                }
                String username = this.getUsernameByAuthSQL(auth);
                String whiteUsername = playerColor == ChessGame.TeamColor.WHITE ? username : game.whiteUsername();
                String blackUsername = playerColor == ChessGame.TeamColor.BLACK ? username : game.blackUsername();
                this.updateGameSQL(game.gameID(), game.gameName(), whiteUsername, blackUsername, game.game());
            } else {
                throw new DataAccessException("Error: Bad Request");
            }
        } else {
            throw new DataAccessException("Error: Unauthorized");
        }
    }

    private Game createGame(String name) {
        id++;
        return new Game(id, null, null, name, new ChessGame());
    }
}
