package dataaccess;

import model.Auth;
import model.User;

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
//            System.out.println("userdao addUser: exceptionThrown");
            throw new DataAccessException("Error: User already exists");
        }
    }

    private Auth createAuth(User user) {
        return new Auth(user.username(), user.hashCode() + "");
    }

    @Override
    public User getUser(User user) {
        User u = database.findUser(user);
        if(u != null) {
            return u;
        } else {
//            System.out.println("userdao getUser: none");
            return null;
        }
    }
}
