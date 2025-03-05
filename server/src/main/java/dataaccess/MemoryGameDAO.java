package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.Collection;

public class MemoryGameDAO implements GameDAO {
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
            database.updateGame(auth.username(), playerColor, gameID);
        } else {
            throw new DataAccessException("Error: Unauthorized");
        }
    }

    private Game createGame(String name, String username) {
        id++;
        return new Game(id, username, "", name, new ChessGame());
    }
}
