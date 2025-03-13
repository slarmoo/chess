package passoff.dataaccess;

import dataaccess.*;
import model.Auth;
import model.Game;
import model.User;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Collection;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MyDatabaseTests {

    private static final SQLDAO SQLDAO = new SQLDAO();
    private static final UserDAO USER_DAO = new MemoryUserDAO();
    private static final GameDAO GAME_DAO = new MemoryGameDAO();
    private static final User USER_1 = new User("user1", "pass1", "email1");
    private static final User USER_2 = new User("user2", "pass2", "email2");
    private Auth auth1;
    private Auth auth2;
    private Game game1;
    private Game game2;

    @BeforeAll
    public static void init() {
    }

    @BeforeEach
    public void setup() {
        SQLDAO.deleteAllSQL();
    }

    @AfterEach
    public void cleanUp() {
    }

    @Test
    @Order(1)
    @DisplayName("Database empty")
    public void checkDatabase() {
        Collection<User> users = SQLDAO.getAllUsersSQL();
        Collection<Auth> auths = SQLDAO.getAllAuthsSQL();
        Collection<Game> games = SQLDAO.getAllGamesSQL();
        Assertions.assertEquals(0, users.size());
        Assertions.assertEquals(0, auths.size());
        Assertions.assertEquals(0, games.size());
    }

    @Test
    @Order(2)
    @DisplayName("Add user")
    public void addUsers() {
        try {
            this.auth1 = USER_DAO.createAuth(USER_1);
            this.auth2 = USER_DAO.createAuth(USER_2);
            SQLDAO.addUserSQL(USER_1, this.auth1);
            SQLDAO.addUserSQL(USER_2, this.auth2);
            Collection<User> users = SQLDAO.getAllUsersSQL();
            Collection<Auth> auths = SQLDAO.getAllAuthsSQL();
            Assertions.assertEquals(2, users.size());
            for (User usercheck : users) {
                if (USER_1.username().equals(usercheck.username())) {
                    Assertions.assertTrue(BCrypt.checkpw(USER_1.password(), usercheck.password()));
                    Assertions.assertEquals(USER_1.email(), usercheck.email());
                } else {
                    Assertions.assertTrue(BCrypt.checkpw(USER_2.password(), usercheck.password()));
                    Assertions.assertEquals(USER_2.email(), usercheck.email());
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
        this.checkDatabase();
    }

    @Test
    @Order(4)
    @DisplayName("logout Users")
    public void logoutUsers() {
        this.addUsers();
        SQLDAO.deleteAuthSQL(this.auth1);
        Collection<Auth> auths = SQLDAO.getAllAuthsSQL();
        Assertions.assertEquals(1, auths.size());
        Assertions.assertTrue(auths.contains(this.auth2));
        Assertions.assertFalse(auths.contains(this.auth1));
        SQLDAO.deleteAuthSQL(this.auth2);
        auths = SQLDAO.getAllAuthsSQL();
        Assertions.assertEquals(0, auths.size());
        Assertions.assertFalse(auths.contains(this.auth1));
        Assertions.assertFalse(auths.contains(this.auth2));
    }

    @Test
    @Order(5)
    @DisplayName("add games")
    public void addGames() {
        this.addUsers();
        try {
            this.game1 = GAME_DAO.addGame(this.auth1, "game1");
            this.game2 = GAME_DAO.addGame(this.auth2, "game2");
            Collection<Game> games = SQLDAO.getAllGamesSQL();
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
            Assertions.assertEquals(this.game1, SQLDAO.getGameByIDSQL(this.game1.gameID()));
            Assertions.assertEquals(this.game2, SQLDAO.getGameByIDSQL(this.game2.gameID()));
            Assertions.assertNull(SQLDAO.getGameByIDSQL(-1));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(7)
    @DisplayName("get username by auth")
    public void getUsernameByAuth() {
        this.addUsers();
        try {
            Assertions.assertEquals(USER_1.username(), SQLDAO.getUsernameByAuthSQL(this.auth1));
            Assertions.assertEquals(USER_2.username(), SQLDAO.getUsernameByAuthSQL(this.auth2));
            Assertions.assertNull(SQLDAO.getUsernameByAuthSQL(new Auth("", "")));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

}
