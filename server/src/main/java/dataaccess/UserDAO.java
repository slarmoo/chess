package dataaccess;
import model.*;

public interface UserDAO {
    Auth addUser(User user) throws DataAccessException;

    Auth createAuth(User user);

    User getUser(User user) throws DataAccessException;

    boolean deleteAuth(Auth auth) throws DataAccessException;

    void addAuth(Auth auth) throws DataAccessException;

    boolean validateAuth(Auth auth) throws DataAccessException;

    String getUsernameByAuthSQL(Auth auth) throws DataAccessException;
}
