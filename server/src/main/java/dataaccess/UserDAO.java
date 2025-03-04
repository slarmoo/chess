package dataaccess;

import model.*;

public interface UserDAO {
    Auth addUser(User user) throws DataAccessException;

    Auth createAuth(User user);

    User getUser(User user);

    boolean deleteAuth(Auth auth);

    void addAuth(Auth auth);

    boolean validateAuth(Auth auth);
}
