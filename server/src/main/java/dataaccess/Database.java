package dataaccess;

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
        System.out.println("addedUser " + userCollection.size());
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
        System.out.println("auth: " + auth);
        for(Auth a : authCollection) {
            System.out.println(a);
        }
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

    public void deleteGames() {
        gameCollection.clear();
    }

    public Collection<Game> getGames() {
        return gameCollection;
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
