package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import model.Auth;
import model.User;

public class Service {
    private final UserDAO userdao;

    public Service(UserDAO userdao) {
        this.userdao = userdao;
    }

    public Auth addUser(User user) throws DataAccessException {
        User u = userdao.getUser(user);
        if(u == null) {
            return userdao.addUser(user);
        } else {
            throw new DataAccessException("User already exists");
        }
    }
}
