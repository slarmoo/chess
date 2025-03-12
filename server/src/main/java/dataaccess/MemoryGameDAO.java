package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.Collection;

public class MemoryGameDAO extends SQLDAO implements GameDAO {
    private final Database database;
    private int id = 0;

    public MemoryGameDAO(Database database) {
        this.database = database;
    }

    @Override
    public Game addGame(Auth auth, String name) throws DataAccessException {
        if(database.validateAuth(auth)) {
            Game game = this.createGame(name, auth.username());
            database.addGame(game);
            return game;
        } else {
            throw new DataAccessException("Error: Unauthorized");
        }
    }

    @Override
    public void deleteAll() {
        database.delete();
    }

    @Override
    public Collection<Game> findAll() {
        return database.getGames();
    }

    @Override
    public void joinGame(Auth auth, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException {
        if(database.validateAuth(auth)) {
            Game game = database.getGameByID(gameID);
            if(game != null) {
                if((playerColor == ChessGame.TeamColor.WHITE && game.whiteUsername() != null) ||
                    (playerColor == ChessGame.TeamColor.BLACK && game.blackUsername() != null)) {
                    throw new DataAccessException("Error: Forbidden");
                }
                String username = database.getUsernameByAuth(auth);
                String whiteUsername = playerColor == ChessGame.TeamColor.WHITE ? username : game.whiteUsername();
                String blackUsername = playerColor == ChessGame.TeamColor.BLACK ? username : game.blackUsername();
                database.updateGame(game.gameID(), game.gameName(), whiteUsername, blackUsername, game.game(), game);
            } else {
                throw new DataAccessException("Error: Bad Request");
            }
        } else {
            throw new DataAccessException("Error: Unauthorized");
        }
    }

    private Game createGame(String name, String username) {
        id++;
        return new Game(id, null, null, name, new ChessGame());
    }
}
