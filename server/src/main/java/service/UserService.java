package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import model.*;

public class UserService extends Service {

    public UserService(UserDAO userdao) {
        super(userdao);
    }
}
