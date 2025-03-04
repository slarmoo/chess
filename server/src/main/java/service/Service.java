package service;

import chess.ChessGame;
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
        if(u != null) {
            Auth a = userdao.createAuth(user);
            userdao.addAuth(a);
            System.out.println("a " + a);
            return a;
        } else {
            throw new DataAccessException("Error: Incorrect credentials");
        }
    }

    public boolean logout(Auth auth) {
        return userdao.deleteAuth(auth);
    }

    public void delete() {
        gamedao.deleteAll();
    }

    public Collection<Game> getGames(Auth auth) throws DataAccessException {
        boolean b = userdao.validateAuth(auth);
        if(b) {
            return gamedao.findAll();
        } else {
            throw new DataAccessException("Error: Unauthorized");
        }
    }

    public Game createGame(Auth auth, String name) throws DataAccessException {
        return gamedao.addGame(auth, name);
    }

    public void joinGame(Auth auth, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException {
        gamedao.joinGame(auth, playerColor, gameID);
    }
}
