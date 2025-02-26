package dataaccess;

import model.Auth;
import model.User;

public class MemoryUserDAO implements UserDAO {
    private final Database database = new Database();

    public Auth addUser(User user) throws DataAccessException {
        database.addUser(user);
        return createAuth(user);
    }

    @Override
    public Auth createAuth(User user) {
        return new Auth(user.username(), user.hashCode() + "");
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
}
