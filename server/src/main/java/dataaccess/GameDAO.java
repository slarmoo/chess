package dataaccess;

import model.*;

import java.util.Collection;

public interface GameDAO {
    Game addGame(Auth auth, String name) throws DataAccessException;

    void deleteAll();

    Collection<Game> findAll();
}
