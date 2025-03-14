package dataaccess;

import chess.ChessGame;
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
        try {
            GAME_DAO.deleteAll();
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @AfterEach
    public void cleanUp() {
    }

    @Test
    @Order(1)
    @DisplayName("Database empty")
    public void checkDatabase() {
        try {
            Collection<User> users = SQLDAO.getAllUsersSQL();
            Collection<Auth> auths = SQLDAO.getAllAuthsSQL();
            Collection<Game> games = GAME_DAO.findAll();
            Assertions.assertEquals(0, users.size());
            Assertions.assertEquals(0, auths.size());
            Assertions.assertEquals(0, games.size());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(2)
    @DisplayName("Add user")
    public void addUsers() {
        try {
            this.auth1 = USER_DAO.addUser(USER_1);
            this.auth2 = USER_DAO.addUser(USER_2);
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
    @Order(2)
    @DisplayName("Add user Fail")
    public void addUsersF() {
        try {
            this.auth1 = USER_DAO.addUser(USER_1);
            USER_DAO.addUser(USER_1);
        } catch (Exception e) {
            Assertions.assertEquals("Error: User already exists", e.getMessage());
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
        try {
            this.addUsers();
            USER_DAO.deleteAuth(this.auth1);
            Collection<Auth> auths = SQLDAO.getAllAuthsSQL();
            Assertions.assertEquals(1, auths.size());
            Assertions.assertTrue(auths.contains(this.auth2));
            Assertions.assertFalse(auths.contains(this.auth1));
            USER_DAO.deleteAuth(this.auth2);
            auths = SQLDAO.getAllAuthsSQL();
            Assertions.assertEquals(0, auths.size());
            Assertions.assertFalse(auths.contains(this.auth1));
            Assertions.assertFalse(auths.contains(this.auth2));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(4)
    @DisplayName("logout Users Fail")
    public void logoutUsersF() {
        try {
            this.addUsers();
            USER_DAO.deleteAuth(this.auth1);
            Collection<Auth> auths = SQLDAO.getAllAuthsSQL();
            Assertions.assertEquals(1, auths.size());
            Assertions.assertTrue(auths.contains(this.auth2));
            Assertions.assertFalse(auths.contains(this.auth1));

            Assertions.assertFalse(USER_DAO.deleteAuth(this.auth1));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(5)
    @DisplayName("add games")
    public void addGames() {
        this.addUsers();
        try {
            this.game1 = GAME_DAO.addGame(this.auth1, "game1");
            this.game2 = GAME_DAO.addGame(this.auth2, "game2");
            Collection<Game> games = GAME_DAO.findAll();
            Assertions.assertTrue(games.contains(this.game1));
            Assertions.assertTrue(games.contains(this.game2));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(5)
    @DisplayName("add games Fail")
    public void addGamesF() {
        try {
            this.game2 = GAME_DAO.addGame(new Auth("", ""), "game3");
        } catch (Exception e) {
            Assertions.assertEquals("Error: Unauthorized", e.getMessage());
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

    @Test
    @Order(8)
    @DisplayName("join game")
    public void joinGame() {
        this.addGames();
        try {
            GAME_DAO.joinGame(this.auth1, ChessGame.TeamColor.WHITE, this.game1.gameID());
            GAME_DAO.joinGame(this.auth2, ChessGame.TeamColor.BLACK, this.game1.gameID());
            Collection<Game> games = GAME_DAO.findAll();
            Assertions.assertTrue(games.contains(new Game(this.game1.gameID(), this.auth1.username(), this.auth2.username(), this.game1.gameName(), this.game1.game())));
            Assertions.assertTrue(games.contains(game2));
            Assertions.assertEquals(2, games.size());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(8)
    @DisplayName("join game Fail")
    public void joinGameF() {
        this.addGames();
        try {
            GAME_DAO.joinGame(this.auth1, ChessGame.TeamColor.WHITE, -1);
        } catch (Exception e) {
            Assertions.assertEquals("Error: Bad Request", e.getMessage());
        }
        try {
            GAME_DAO.joinGame(new Auth ("", ""), ChessGame.TeamColor.BLACK, this.game1.gameID());
        } catch (Exception e) {
            Assertions.assertEquals("Error: Unauthorized", e.getMessage());
        }
        try {
            GAME_DAO.joinGame(this.auth1, ChessGame.TeamColor.BLACK, this.game1.gameID());
            GAME_DAO.joinGame(this.auth2, ChessGame.TeamColor.BLACK, this.game1.gameID());
        } catch (Exception e) {
            Assertions.assertEquals("Error: Forbidden", e.getMessage());
        }
    }

}
