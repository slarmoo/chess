package dataaccess;

import model.Auth;
import model.User;

import java.util.UUID;

public class MemoryUserDAO extends SQLDAO implements UserDAO {

    public Auth addUser(User user) throws DataAccessException {
        if(getUser(user) == null) {
            try {
                Auth auth = createAuth(user);
                this.addUserSQL(user, auth);
                return auth;
            } catch (Exception e) {
                throw new DataAccessException("Error: " + e.getMessage());
            }
        } else {
            throw new DataAccessException("Error: User already exists");
        }
    }

    public Auth createAuth(User user) {
        return new Auth(user.username(), UUID.randomUUID() + "");
    }

    public void addAuth(Auth auth) throws DataAccessException {
        this.addAuthSQL(auth);
    }

    @Override
    public User getUser(User user) throws DataAccessException {
        return this.getUserSQL(user);
    }

    @Override
    public boolean deleteAuth(Auth auth) throws DataAccessException {
        if(validateAuth(auth)) {
            this.deleteAuthSQL(auth);
            return true;
        }
        return false;
    }

    @Override
    public boolean validateAuth(Auth auth) throws DataAccessException {
        return this.validateAuthSQL(auth);
    }
}
