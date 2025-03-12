package dataaccess;

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

//    private void

}
