package service;

import chess.ChessGame;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import org.junit.jupiter.api.*;
import dataaccess.*;

import java.util.*;

import model.Auth;
import model.User;
import model.Game;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MyAPITests {
    private Service service;
    private static User existingUser;
    private static User newUser;
    private Auth existingUserAuth;
    private final SQLDAO sqldao = new SQLDAO();

    @BeforeAll
    public static void init() {

        existingUser = new User("ExistingUser", "existingUserPassword", "eu@mail.com");

        newUser = new User("NewUser", "newUserPassword", "nu@mail.com");

    }

    @BeforeEach
    public void setup() {
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        try {
            gameDAO.deleteAll();
            existingUserAuth = userDAO.addUser(existingUser);
            service = new service.Service(userDAO, gameDAO);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @AfterEach
    public void cleanUp() {
    }

    @Test
    @Order(1)
    @DisplayName("Database has existing user")
    public void checkDatabase() {
        try {
            User sqluser = sqldao.getUserSQL(existingUser);
            Assertions.assertEquals(existingUser, sqluser);
            Assertions.assertTrue(sqldao.getAllAuthsSQL().contains(existingUserAuth));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(2)
    @DisplayName("Successful Login")
    public void login() {
        try {
            Auth a = service.login(existingUser);
            Assertions.assertInstanceOf(Auth.class, a);
            Assertions.assertEquals(existingUser, sqldao.getUserSQL(existingUser));
            Assertions.assertEquals(existingUser.email(), sqldao.getUserSQL(existingUser).email());
            Assertions.assertTrue(sqldao.getAllAuthsSQL().contains(a));
        } catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(2)
    @DisplayName("Unsuccessful Login")
    public void loginF() {
        try {
            service.login(newUser);
        } catch (DataAccessException e) {
            Assertions.assertEquals("Error: Incorrect credentials", e.getMessage());
        }
    }

    @Test
    @Order(3)
    @DisplayName("Successful Logout")
    public void logout() {
        try {
            service.logout(existingUserAuth);
            Assertions.assertEquals(existingUser, sqldao.getUserSQL(existingUser));
            Assertions.assertFalse(sqldao.getAllAuthsSQL().contains(existingUserAuth));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(4)
    @DisplayName("Successful Register")
    public void register() {
        try {
            Auth a = service.addUser(newUser);
            Assertions.assertInstanceOf(Auth.class, a);
            Assertions.assertEquals(newUser, sqldao.getUserSQL(newUser));
            Assertions.assertTrue(sqldao.getAllAuthsSQL().contains(a));
        } catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(4)
    @DisplayName("Unsuccessful Register")
    public void registerF() {
        try {
            service.addUser(existingUser);
        } catch (DataAccessException e) {
            Assertions.assertEquals("Error: User already exists", e.getMessage());
        }
    }

    @Test
    @Order(5)
    @DisplayName("Successful Register then logout")
    public void registerLogout() {
        try {
            Auth a = service.addUser(newUser);
            Assertions.assertInstanceOf(Auth.class, a);
            Assertions.assertEquals(newUser, sqldao.getUserSQL(newUser));
            Assertions.assertTrue(sqldao.getAllAuthsSQL().contains(a));
            service.logout(a);
            Assertions.assertEquals(newUser, sqldao.getUserSQL(newUser));
            Assertions.assertFalse(sqldao.getAllAuthsSQL().contains(a));
        } catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(6)
    @DisplayName("Successful Register then logout then login")
    public void registerLogoutLogin() {
        try {
            System.out.println("register");
            Auth a = service.addUser(newUser);
            Assertions.assertInstanceOf(Auth.class, a);
            Assertions.assertEquals(newUser, sqldao.getUserSQL(newUser));
            Assertions.assertTrue(sqldao.getAllAuthsSQL().contains(a));
            System.out.println("logout");
            service.logout(a);
            Assertions.assertEquals(newUser, sqldao.getUserSQL(newUser));
            Assertions.assertFalse(sqldao.getAllAuthsSQL().contains(a));
            System.out.println("login");
            Auth b = service.login(newUser);
            Assertions.assertInstanceOf(Auth.class, b);
            Assertions.assertEquals(newUser, sqldao.getUserSQL(newUser));
            Assertions.assertTrue(sqldao.getAllAuthsSQL().contains(b));
            Assertions.assertNotEquals(a, b);
        } catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(7)
    @DisplayName("Successful Create Game")
    public void createGame() {
        try {
            Game g = service.createGame(existingUserAuth, "gameName");
            Assertions.assertInstanceOf(Game.class, g);
            Assertions.assertEquals("gameName", g.gameName());
            Assertions.assertEquals(new ChessGame().getBoard(), g.game().getBoard());
        } catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(7)
    @DisplayName("Unsuccessful Create Game")
    public void createGameF() {
        try {
            service.createGame(new Auth("", ""), "");
        } catch (DataAccessException e) {
            Assertions.assertEquals("Error: Unauthorized", e.getMessage());
        }
    }

    @Test
    @Order(8)
    @DisplayName("Successful Join Game")
    public void joinGame() {
        try {
            Game g = service.createGame(existingUserAuth, "gameName");
            service.joinGame(existingUserAuth, ChessGame.TeamColor.BLACK, g.gameID());
            Collection<Game> games = sqldao.getAllGamesSQL();
            Assertions.assertEquals(1, games.size());
            var databaseGame = games.toArray()[0];
            Assertions.assertInstanceOf(Game.class, databaseGame);
            Game g2 = (Game) databaseGame;
            Assertions.assertEquals(g.game().getBoard(), g2.game().getBoard());
            Assertions.assertEquals(g.gameID(), g2.gameID());
            Assertions.assertEquals(g.gameName(), g2.gameName());
            Assertions.assertEquals(existingUserAuth.username(), g2.blackUsername());
            Assertions.assertNull(g2.whiteUsername());
        } catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(8)
    @DisplayName("Unsuccessful Join Game")
    public void joinGameF() {
        try {
            Game g = service.createGame(existingUserAuth, "gameName");
            service.joinGame(existingUserAuth, ChessGame.TeamColor.BLACK, g.gameID() * 100);
        } catch (DataAccessException e) {
            Assertions.assertEquals("Error: Bad Request", e.getMessage());
        }
    }

    @Test
    @Order(9)
    @DisplayName("Successful Find Games")
    public void findGames() {
        try {
            Collection<Game> gamesList = new ArrayList<>();
            for(int i = 0; i < 5; i++) {
                gamesList.add(service.createGame(existingUserAuth, "game" + i));
            }

            Collection<Game> otherGamesList = service.getGames(existingUserAuth);

            Assertions.assertEquals(gamesList, otherGamesList);
        } catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(9)
    @DisplayName("Unsuccessful Join Game")
    public void findGamesF() {
        try {
            for(int i = 0; i < 5; i++) {
                service.createGame(existingUserAuth, "game" + i);
            }
            service.getGames(new Auth("", ""));
        } catch (DataAccessException e) {
            Assertions.assertEquals("Error: Unauthorized", e.getMessage());
        }
    }

    @Test
    @Order(10)
    @DisplayName("Successful Clear Database")
    public void clearDatabase() {
        try {
            for(int i = 0; i < 10; i++) {
                service.createGame(existingUserAuth, "game" + i);
            }

            service.delete();

            Assertions.assertEquals(0, sqldao.getAllAuthsSQL().size());
            Assertions.assertEquals(0, sqldao.getAllUsersSQL().size());
            Assertions.assertEquals(0, sqldao.getAllGamesSQL().size());
        } catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

}
