package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.*;

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
            `id` int NOT NULL AUTO_INCREMENT,
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

    //borrowed from petshop
    private int executeUpdate(String statement, Object... params) throws ResponseException {
        try {
            var conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS);
            for (var i = 0; i < params.length; i++) {
                var param = params[i];
                if (param instanceof String p) ps.setString(i + 1, p);
                else if (param instanceof Integer p) ps.setInt(i + 1, p);
                else if (param instanceof ChessGame p) ps.setString(i + 1, p.toString());
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

    public void addUserSQL(User user, Auth auth) throws ResponseException {
        String statement = "insert into user (username, password, email) values (?, ?, ?)";
        executeUpdate(statement, user.username(), user.password(), user.email());
        statement = "insert into auth (username, authToken) values (?, ?)";
        executeUpdate(statement, auth.username(), auth.authToken());
    }

    public Collection<User> getAllUsers() {
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

    private User parseUser(ResultSet response) throws SQLException {
        return new User(response.getString("username"), response.getString("password"), response.getString("email"));
    }

}
