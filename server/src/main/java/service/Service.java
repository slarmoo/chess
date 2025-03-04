package service;

import dataaccess.*;
import model.*;

import java.util.Collection;

public class Service {
    private final UserDAO userdao;
    private final GameDAO gamedao;

    public Service(UserDAO userdao, GameDAO gamedao) {
        this.userdao = userdao;
        this.gamedao = gamedao;
    }

    public Auth addUser(User user) throws DataAccessException {
        User u = userdao.getUser(user);
        if(u == null) {
            return userdao.addUser(user);
        } else {
            throw new DataAccessException("Error: User already exists");
        }
    }

    public Auth login(User user) throws DataAccessException {
        User u = userdao.getUser(user);
        System.out.println(u);
        if(u != null) {
            Auth a = userdao.createAuth(user);
            userdao.addAuth(a);
            return a;
        } else {
            throw new DataAccessException("Error: Incorrect credentials");
        }
    }

    public void logout(Auth auth) {
        userdao.deleteAuth(auth);
    }

    public void delete() {
        gamedao.deleteAll();
    }

    public Collection<Game> getGames() {
        return gamedao.findAll();
    }

    public Game createGame(Auth auth, String name) throws DataAccessException {
        return gamedao.addGame(auth, name);
    }
}
