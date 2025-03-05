package dataaccess;

import model.Auth;
import model.User;

import java.util.UUID;

public class MemoryUserDAO implements UserDAO {
    private final Database database;

    public MemoryUserDAO(Database database) {
        this.database = database;
    }

    public Auth addUser(User user) throws DataAccessException {
        if(getUser(user) == null) {
            Auth auth = createAuth(user);
            database.addUser(user, auth);
            return auth;
        } else {
            throw new DataAccessException("Error: User already exists");
        }
    }

    public Auth createAuth(User user) {
        return new Auth(user.username(), UUID.randomUUID() + "");
    }

    public void addAuth(Auth auth) {
        database.addAuth(auth);
    }

    @Override
    public User getUser(User user) {
        User u = database.findUser(user);
        if(u != null) {
            return u;
        } else {
            return null;
        }
    }

    @Override
    public boolean deleteAuth(Auth auth) {
        if(validateAuth(auth)) {
            database.deleteAuth(auth);
            return true;
        }
        return false;
    }

    @Override
    public boolean validateAuth(Auth auth) {
        return database.validateAuth(auth);
    }
}
