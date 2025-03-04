package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.Collection;
import java.util.List;

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
        database.deleteGames();
    }

    @Override
    public Collection<Game> findAll() {
        return database.getGames();
    }

    private Game createGame(String name, String username) {
        id++;
        return new Game(id, username, "", name, new ChessGame());
    }
}
