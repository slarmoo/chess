package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import org.mindrot.jbcrypt.BCrypt;
import model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLDAO {

    public SQLDAO() {
        SQLDAO.configureDatabase();
    }

    private static final String[] CREATE_STATEMENTS = {
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

    public static void configureDatabase() {
        try {
            DatabaseManager.createDatabase();
            var conn = DatabaseManager.getConnection();
            for (var statement : CREATE_STATEMENTS) {
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

    public void addAuthSQL(Auth auth) throws DataAccessException {
        String statement = "insert into auth (username, authToken) values (?, ?)";
        try {
            executeUpdate(statement, auth.username(), auth.authToken());
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void addGameSQL(Game game) throws DataAccessException {
        String statement = "insert into game (id, whiteUsername, blackUsername, gameName, chessGame) values (?, ?, ?, ?, ?)";
        try {
            executeUpdate(statement, game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public Collection<User> getAllUsersSQL() throws DataAccessException {
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
            throw new DataAccessException(e.getMessage());
        }
        return users;
    }

    public User getUserSQL(User user) throws DataAccessException {
        Collection<User> users = this.getAllUsersSQL();
        if(users.contains(user)) {
            return user;
        } else {
            for(User u : users) {
                if(u.username().equals(user.username()) && BCrypt.checkpw(user.password(), u.password())) {
                    return user;
                }
            }
        }
        return null;
    }

    public Collection<Auth> getAllAuthsSQL() throws DataAccessException {
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
            throw new DataAccessException(e.getMessage());
        }
        return auths;
    }

    public boolean validateAuthSQL(Auth auth) throws DataAccessException {
        return this.getAllAuthsSQL().contains(auth);
    }

    public void deleteAuthSQL(Auth auth) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken=?";
        try {
            executeUpdate(statement, auth.authToken());
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public Collection<Game> getAllGamesSQL() throws DataAccessException {
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
            throw new DataAccessException(e.getMessage());
        }
        return games;
    }

    public Game getGameByIDSQL(int id) throws DataAccessException {
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
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    public String getUsernameByAuthSQL(Auth auth) throws DataAccessException {
        try {
            var conn = DatabaseManager.getConnection();
            String statement = "select username, authToken from auth where authToken=?";
            var preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, auth.authToken());
            var response = preparedStatement.executeQuery();
            if(response.next()) {
                return parseAuth(response).username();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    public void updateGameSQL(int gameID, String gameName, String whiteUsername,
                              String blackUsername, ChessGame chessGame) throws DataAccessException {
        var statement = "DELETE FROM game WHERE gameName=?";
        try {
            executeUpdate(statement, gameName);
            Game updatedGame = new Game(gameID, whiteUsername, blackUsername, gameName, chessGame);
            this.addGameSQL(updatedGame);
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void deleteAllSQL() throws DataAccessException {
        try {
            var statement = "truncate user";
            executeUpdate(statement);
            statement = "truncate auth";
            executeUpdate(statement);
            statement = "truncate game";
            executeUpdate(statement);
        } catch(Exception e) {
            throw new DataAccessException(e.getMessage());
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
    private void executeUpdate(String statement, Object... params) throws ResponseException {
        try {
            var conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS);
            for (var i = 0; i < params.length; i++) {
                var param = params[i];
                if (param instanceof String p) {
                    ps.setString(i + 1, p);
                } else if (param instanceof Integer p) {
                    ps.setInt(i + 1, p);
                } else if (param instanceof ChessGame p) {
                    ps.setString(i + 1, new Gson().toJson(p));
                } else if (param == null) {
                    ps.setNull(i + 1, NULL);
                }
            }
            ps.executeUpdate();

            var rs = ps.getGeneratedKeys();
            if (rs.next()) {
                rs.getInt(1);
            }

        } catch (Exception e) {
            throw new ResponseException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

}
