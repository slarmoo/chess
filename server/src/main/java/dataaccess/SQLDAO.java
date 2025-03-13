package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLDAO {

    private static final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auth (
            `username` varchar(20) NOT NULL,
            `authToken` varChar(63) NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS  user (
            `username` varchar(20) NOT NULL,
            `password` varChar(63) NOT NULL,
            `email` varChar(63) NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS  game (
            `id` int NOT NULL,
            `whiteUsername` varchar(20),
            `blackUsername` varchar(20),
            `gameName` varchar(20) NOT NULL,
            `chessGame` text NOT NULL,
            PRIMARY KEY (`id`)
            )
            """
    };

    static {
        try (var conn = DatabaseManager.getConnection()) {
            DatabaseManager.createDatabase();
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
//            throw new DataAccessException(String.format("Unable to configure database: %s", e.getMessage()));
        }
    }

    public void addUserSQL(User user, Auth auth) throws ResponseException {
        String passwordHashed = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        String statement = "insert into user (username, password, email) values (?, ?, ?)";
        executeUpdate(statement, user.username(), passwordHashed, user.email());
        statement = "insert into auth (username, authToken) values (?, ?)";
        executeUpdate(statement, auth.username(), auth.authToken());
    }

    public void addAuthSQL(Auth auth) {
        String statement = "insert into auth (username, authToken) values (?, ?)";
        try {
            executeUpdate(statement, auth.username(), auth.authToken());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void addGameSQL(Game game) {
        String statement = "insert into game (id, whiteUsername, blackUsername, gameName, chessGame) values (?, ?, ?, ?, ?)";
        try {
            executeUpdate(statement, game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public Collection<User> getAllUsersSQL() {
        var users = new ArrayList<User>();
        try {
            var conn = DatabaseManager.getConnection();
            String statement = "select username, password, email from user";
            var preparedStatement = conn.prepareStatement(statement);
            var response = preparedStatement.executeQuery();
            while(response.next()) {
                users.add(parseUser(response));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return users;
    }

    public User getUserSQL(User user) {
        Collection<User> users = this.getAllUsersSQL();
        if(users.contains(user)) {
            return user;
        }
        return null;
    }

    public Collection<Auth> getAllAuthsSQL() {
        var auths = new ArrayList<Auth>();
        try {
            var conn = DatabaseManager.getConnection();
            String statement = "select username, authToken from auth";
            var preparedStatement = conn.prepareStatement(statement);
            var response = preparedStatement.executeQuery();
            while(response.next()) {
                auths.add(parseAuth(response));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return auths;
    }

    public boolean validateAuthSQL(Auth auth) {
        return this.getAllAuthsSQL().contains(auth);
    }

    public void deleteAuthSQL(Auth auth) {
        var statement = "DELETE FROM auth WHERE authToken=?";
        try {
            executeUpdate(statement, auth.authToken());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public Collection<Game> getAllGamesSQL() {
        var games = new ArrayList<Game>();
        try {
            var conn = DatabaseManager.getConnection();
            String statement = "select id, whiteUsername, blackUsername, gameName, chessGame from game";
            var preparedStatement = conn.prepareStatement(statement);
            var response = preparedStatement.executeQuery();
            while(response.next()) {
                games.add(parseGame(response));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return games;
    }

    public Game getGameByIDSQL(int id) {
        try {
            var conn = DatabaseManager.getConnection();
            String statement = "select id, whiteUsername, blackUsername, gameName, chessGame from game where id=?";
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setInt(1, id);
            var response = preparedStatement.executeQuery();
            if(response.next()) {
                return parseGame(response);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public void deleteAllSQL() {
        try {
            var statement = "truncate user";
            executeUpdate(statement);
            statement = "truncate auth";
            executeUpdate(statement);
            statement = "truncate game";
            executeUpdate(statement);
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    private User parseUser(ResultSet response) throws SQLException {
        return new User(response.getString("username"), response.getString("password"), response.getString("email"));
    }

    private Auth parseAuth(ResultSet response) throws SQLException {
        return new Auth(response.getString("username"), response.getString("authToken"));
    }

    private Game parseGame(ResultSet response) throws SQLException {
        return new Game(response.getInt("id"), response.getString("whiteUsername"), response.getString("blackUsername"),
                response.getString("gameName"), new Gson().fromJson(response.getString("chessGame"), ChessGame.class));
    }

    //borrowed from petshop
    private int executeUpdate(String statement, Object... params) throws ResponseException {
        try {
            var conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS);
            for (var i = 0; i < params.length; i++) {
                var param = params[i];
                if (param instanceof String p) ps.setString(i + 1, p);
                else if (param instanceof Integer p) ps.setInt(i + 1, p);
                else if (param instanceof ChessGame p) ps.setString(i + 1, new Gson().toJson(p));
                else if (param == null) ps.setNull(i + 1, NULL);
            }
            ps.executeUpdate();

            var rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;
        } catch (Exception e) {
            throw new ResponseException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

}
