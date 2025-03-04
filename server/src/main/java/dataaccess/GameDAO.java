package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.Collection;

public interface GameDAO {
    Game addGame(Auth auth, String name) throws DataAccessException;

    void deleteAll();

    Collection<Game> findAll();

    void joinGame(Auth auth, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException;
}
