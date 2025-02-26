package model;

import com.google.gson.Gson;

public record User (String username, String password, String email) {

    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public int hashCode() {
        return username.hashCode() + password.hashCode() + email.hashCode();
    }
}
