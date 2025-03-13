package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.ArrayList;
import java.util.Collection;

//fake database for phase 3
public class Database {
    public final Collection<User> userCollection = new ArrayList<>();
    public final Collection<Game> gameCollection = new ArrayList<>();
    public final Collection<Auth> authCollection = new ArrayList<>();

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

    public Auth findAuth(Auth auth) {
        for(Auth a : authCollection) {
            if(a.equals(auth)) {
                return a;
            }
        }
        return null;
    }



    public Collection<Game> getGames() {
        return gameCollection;
    }

    public void updateGame(int gameID, String gameName, String whiteUsername, String blackUsername, ChessGame chessGame, Game oldGame) {
        gameCollection.remove(oldGame);
        Game updatedGame = new Game(gameID, whiteUsername, blackUsername, gameName, chessGame);
        gameCollection.add(updatedGame);

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
