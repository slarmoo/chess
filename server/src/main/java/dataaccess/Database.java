package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.ArrayList;
import java.util.Collection;

//fake database for phase 3
public class Database {
    private final Collection<User> userCollection = new ArrayList<>();
    private final Collection<Game> gameCollection = new ArrayList<>();
    private final Collection<Auth> authCollection = new ArrayList<>();

    public void addUser(User user, Auth auth) {
        userCollection.add(user);
        authCollection.add(auth);
    }

    public User findUser(User user) {
        this.printUserDatabase();
        if(userCollection.contains(user)) {
            return user;
        } else {
            return null;
        }
    }

    public void addGame(Game game) {
        gameCollection.add(game);
    }

    public boolean validateAuth(Auth auth) {
        return authCollection.contains(auth);
    }

    public Auth findAuth(Auth auth) {
        for(Auth a : authCollection) {
            if(a.equals(auth)) {
                return a;
            }
        }
        return null;
    }

    public void deleteAuth(Auth auth) {
        authCollection.remove(auth);
    }

    public void addAuth(Auth auth) {
        authCollection.add(auth);
    }

    public void delete() {
        gameCollection.clear();
        userCollection.clear();
        authCollection.clear();
    }

    public Collection<Game> getGames() {
        return gameCollection;
    }

    public void updateGame(String username, ChessGame.TeamColor playerColor, int gameID) {
        Game g = getGameByID(gameID);
        if(g != null) {
            String whiteUsername = playerColor == ChessGame.TeamColor.WHITE ? username : g.whiteUsername();
            String blackUsername = playerColor != ChessGame.TeamColor.WHITE ? username : g.whiteUsername();
            Game updatedGame = new Game(g.gameID(), whiteUsername, blackUsername, g.gameName(), g.game());
            gameCollection.add(updatedGame);
        }
    }

    private Game getGameByID(int ID) {
        for(Game g : gameCollection) {
            if(g.gameID() == ID) {
                gameCollection.remove(g);
                return g;
            }
        }
        return null;
    }

    private void printUserDatabase() {
        int inc = 0;
        for(User u : userCollection) {
            System.out.println("User" + inc + " " + u);
            inc++;
        }
        if(inc == 0) {
            System.out.println("UserDatabase empty");
        }
    }
}
