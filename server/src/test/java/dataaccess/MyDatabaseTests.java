package dataaccess;

import chess.ChessGame;
import model.Auth;
import model.Game;
import model.User;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import service.Service;

import java.util.ArrayList;
import java.util.Collection;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MyDatabaseTests {

    private static final SQLDAO sqldao = new SQLDAO();
    private static final UserDAO userdao = new MemoryUserDAO();
    private static final GameDAO gamedao = new MemoryGameDAO(new Database());
    private static final User user1 = new User("user1", "pass1", "email1");
    private static final User user2 = new User("user2", "pass2", "email2");
    private Auth auth1;
    private Auth auth2;
    private Game game1;
    private Game game2;

    @BeforeAll
    public static void init() {
    }

    @BeforeEach
    public void setup() {
        sqldao.deleteAllSQL();
    }

    @AfterEach
    public void cleanUp() {
    }

    @Test
    @Order(1)
    @DisplayName("Database empty")
    public void checkDatabase() {
        Collection<User> users = sqldao.getAllUsersSQL();
        Collection<Auth> auths = sqldao.getAllAuthsSQL();
        Collection<Game> games = sqldao.getAllGamesSQL();
        Assertions.assertEquals(0, users.size());
        Assertions.assertEquals(0, auths.size());
        Assertions.assertEquals(0, games.size());
    }

    @Test
    @Order(2)
    @DisplayName("Add user")
    public void addUser() {
        try {
            this.auth1 = userdao.createAuth(user1);
            this.auth2 = userdao.createAuth(user2);
            sqldao.addUserSQL(user1, this.auth1);
            sqldao.addUserSQL(user2, this.auth2);
            Collection<User> users = sqldao.getAllUsersSQL();
            Collection<Auth> auths = sqldao.getAllAuthsSQL();
            Assertions.assertEquals(2, users.size());
            for (User usercheck : users) {
                if (user1.username().equals(usercheck.username())) {
                    Assertions.assertTrue(BCrypt.checkpw(user1.password(), usercheck.password()));
                    Assertions.assertEquals(user1.email(), usercheck.email());
                } else {
                    Assertions.assertTrue(BCrypt.checkpw(user2.password(), usercheck.password()));
                    Assertions.assertEquals(user2.email(), usercheck.email());
                }
            }
            Assertions.assertEquals(2, auths.size());
            Assertions.assertTrue(auths.contains(this.auth1));
            Assertions.assertTrue(auths.contains(this.auth2));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(3)
    @DisplayName("Database still empty")
    public void checkDatabaseAgain() {
        Collection<User> users = sqldao.getAllUsersSQL();
        Collection<Auth> auths = sqldao.getAllAuthsSQL();
        Collection<Game> games = sqldao.getAllGamesSQL();
        Assertions.assertEquals(0, users.size());
        Assertions.assertEquals(0, auths.size());
        Assertions.assertEquals(0, games.size());
    }

    @Test
    @Order(4)
    @DisplayName("logout Users")
    public void logoutUsers() {
        this.addUser();
        sqldao.deleteAuthSQL(this.auth1);
        Collection<Auth> auths = sqldao.getAllAuthsSQL();
        Assertions.assertEquals(1, auths.size());
        Assertions.assertTrue(auths.contains(this.auth2));
        Assertions.assertFalse(auths.contains(this.auth1));
        sqldao.deleteAuthSQL(this.auth2);
        auths = sqldao.getAllAuthsSQL();
        Assertions.assertEquals(0, auths.size());
        Assertions.assertFalse(auths.contains(this.auth1));
        Assertions.assertFalse(auths.contains(this.auth2));
    }

    @Test
    @Order(5)
    @DisplayName("add games")
    public void addGames() {
        this.addUser();
        try {
            this.game1 = gamedao.addGame(this.auth1, "game1");
            this.game2 = gamedao.addGame(this.auth2, "game2");
            Collection<Game> games = sqldao.getAllGamesSQL();
            Assertions.assertTrue(games.contains(this.game1));
            Assertions.assertTrue(games.contains(this.game2));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(6)
    @DisplayName("get game by id")
    public void getGameByID() {
        this.addGames();
        try {
            Assertions.assertEquals(this.game1, sqldao.getGameByIDSQL(this.game1.gameID()));
            Assertions.assertEquals(this.game2, sqldao.getGameByIDSQL(this.game2.gameID()));
            Assertions.assertNull(sqldao.getGameByIDSQL(-1));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

}
