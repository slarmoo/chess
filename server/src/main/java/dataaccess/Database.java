package dataaccess;

import model.User;

import java.util.ArrayList;
import java.util.Collection;

//fake database for phase 3
public class Database {
    private final Collection<User> userCollection = new ArrayList<>();

    public void addUser(User user) {
        userCollection.add(user);
    }

    public User findUser(User user) {
        for(User u : userCollection) {
            if(u == user) {
                return u;
            }
        }
        return null;
    }
}
