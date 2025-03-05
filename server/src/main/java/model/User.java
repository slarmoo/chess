package model;

import com.google.gson.Gson;

import java.util.Objects;

public record User (String username, String password, String email) {

    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User(String username1, String password1, String email1))) {
            return false;
        }
//      We don't care about the equivalence of emails
        return /*Objects.equals(email, email1) &&*/ Objects.equals(username, username1) && Objects.equals(password, password1);
    }
}
