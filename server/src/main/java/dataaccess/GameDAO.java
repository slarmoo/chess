package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.Collection;

public interface GameDAO {
    Game addGame(Auth auth, String name) throws DataAccessException;

    void deleteAll() throws DataAccessException;

    Collection<Game> findAll() throws DataAccessException;

    void joinGame(Auth auth, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException;

    void updateGame(ChessGame.TeamColor playerColor, ChessGame game, int gameID);
}
