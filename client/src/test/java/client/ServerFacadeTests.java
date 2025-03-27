package client;

import chess.ChessGame;
import model.Auth;
import model.Game;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void emptyDatabase() {
        ServerFacade.emptyDatabase();
    }


    @Test
    @Order(1)
    @DisplayName("Successful Register")
    public void registerS() {
        Object obj = ServerFacade.register("a", "a", "a");
        Assertions.assertInstanceOf(Auth.class, obj);
    }

    @Test
    @Order(2)
    @DisplayName("Unsuccessful Register")
    public void registerF() {
        ServerFacade.register("a", "a", "a");
        Object obj = ServerFacade.register("a", "a", "a"); //already registered
        Assertions.assertFalse(obj instanceof Auth);
    }

    @Test
    @Order(3)
    @DisplayName("Successful Login")
    public void loginS() {
        ServerFacade.register("a", "a", "a");
        Object obj = ServerFacade.login("a", "a");
        Assertions.assertInstanceOf(Auth.class, obj);
    }

    @Test
    @Order(4)
    @DisplayName("Unsuccessful Login")
    public void loginF() {
        ServerFacade.register("a", "a", "a");
        Object obj = ServerFacade.login("b", "b"); //user doesn't exist
        Assertions.assertFalse(obj instanceof Auth);
        obj = ServerFacade.login("a", "b"); //incorrect credentials
        Assertions.assertFalse(obj instanceof Auth);
    }

    @Test
    @Order(5)
    @DisplayName("Successful Create Game")
    public void createGameS() {
        Object obj = ServerFacade.register("a", "a", "a");
        Assertions.assertInstanceOf(Auth.class, obj);
        Auth auth = (Auth) obj;
        obj = ServerFacade.createGame("test", auth);
        Assertions.assertInstanceOf(Game.class, obj);
    }

    @Test
    @Order(6)
    @DisplayName("Unsuccessful Create Game")
    public void createGameF() {
        Object obj = ServerFacade.createGame("test", new Auth("", "")); //unauthorized
        Assertions.assertFalse(obj instanceof Game);
    }

    @Test
    @Order(7)
    @DisplayName("Successful List Games")
    public void listGamesS() {
        Object obj = ServerFacade.register("a", "a", "a");
        Assertions.assertInstanceOf(Auth.class, obj);
        Auth auth = (Auth) obj;
        obj = ServerFacade.createGame("test1", auth);
        Assertions.assertInstanceOf(Game.class, obj);
        obj = ServerFacade.createGame("test2", auth);
        Assertions.assertInstanceOf(Game.class, obj);
        obj = ServerFacade.createGame("test3", auth);
        Assertions.assertInstanceOf(Game.class, obj);
        String observed = ServerFacade.getGames(auth);
        Assertions.assertNotNull(observed);
        String expected =   ServerFacade.parseGame(createTestGame(1), 1) + "\n" +
                            ServerFacade.parseGame(createTestGame(2), 2) + "\n" +
                            ServerFacade.parseGame(createTestGame(3), 3) + "\n";
        Assertions.assertEquals(expected, observed);
    }

    @Test
    @Order(8)
    @DisplayName("Successful List Games Empty")
    public void listGamesE() {
        Object obj = ServerFacade.register("a", "a", "a");
        Assertions.assertInstanceOf(Auth.class, obj);
        Auth auth = (Auth) obj;
        String observed = ServerFacade.getGames(auth);
        Assertions.assertNotNull(observed);
        Assertions.assertEquals("[empty] \n", observed);
    }

    @Test
    @Order(9)
    @DisplayName("Unsuccessful List Games")
    public void listGamesF() {
        Object obj = ServerFacade.register("a", "a", "a");
        Assertions.assertInstanceOf(Auth.class, obj);
        Auth auth = (Auth) obj;
        obj = ServerFacade.createGame("test1", auth);
        String observed = ServerFacade.getGames(new Auth("", ""));
        Assertions.assertNull(observed);
    }

    @Test
    @Order(10)
    @DisplayName("Successful Spectate")
    public void spectateS() {
        Object obj = ServerFacade.register("a", "a", "a");
        Assertions.assertInstanceOf(Auth.class, obj);
        Auth auth = (Auth) obj;
        obj = ServerFacade.createGame("test1", auth);
        try {
            Game game = ServerFacade.grabGameWithID(1, auth);
            Assertions.assertNotNull(game);
            Assertions.assertEquals("test1", game.gameName());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(11)
    @DisplayName("Unsuccessful Spectate")
    public void spectateF() {
        Object obj = ServerFacade.register("a", "a", "a");
        Assertions.assertInstanceOf(Auth.class, obj);
        Auth auth = (Auth) obj;
        obj = ServerFacade.createGame("test1", auth);
        try {
            Game game = ServerFacade.grabGameWithID(2, auth);
            Assertions.assertNull(game);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
        try {
            ServerFacade.grabGameWithID(1, new Auth("", ""));
        } catch (Exception e) {
            Assertions.assertEquals("Server returned HTTP response code: 401 for URL: http://localhost:8081/game", e.getMessage());
        }
    }

    @Test
    @Order(12)
    @DisplayName("Successful Join Game")
    public void joinGameS() {
        Object obj = ServerFacade.register("a", "a", "a");
        Assertions.assertInstanceOf(Auth.class, obj);
        Auth auth = (Auth) obj;
        obj = ServerFacade.createGame("test1", auth);
        obj = ServerFacade.joinGame(1, ChessGame.TeamColor.BLACK, auth);
        Assertions.assertInstanceOf(Game.class, obj);
        Game game = (Game) obj;
        Assertions.assertEquals("test1", game.gameName());
        Assertions.assertEquals("a", game.blackUsername());
    }

    @Test
    @Order(13)
    @DisplayName("Unsuccessful Join Game")
    public void joinGameF() {
        Object obj = ServerFacade.register("a", "a", "a");
        Assertions.assertInstanceOf(Auth.class, obj);
        Auth auth = (Auth) obj;
        obj = ServerFacade.createGame("test1", auth);
        obj = ServerFacade.joinGame(1, ChessGame.TeamColor.BLACK, auth); //should succeed
        Assertions.assertInstanceOf(Game.class, obj);
        Game game = (Game) obj;
        Assertions.assertEquals("test1", game.gameName());
        Assertions.assertEquals("a", game.blackUsername());
        obj = ServerFacade.joinGame(1, ChessGame.TeamColor.BLACK, auth); //joining taken place
        Assertions.assertFalse(obj instanceof Game);
        obj = ServerFacade.joinGame(2, ChessGame.TeamColor.BLACK, auth); //joining game that doesn't exist
        Assertions.assertFalse(obj instanceof Game);
        obj = ServerFacade.joinGame(1, ChessGame.TeamColor.WHITE, new Auth("", "")); //joining with invalid auth
        Assertions.assertFalse(obj instanceof Game);
    }

    @Test
    @Order(14)
    @DisplayName("Successful Logout")
    public void logoutS() {
        Object obj = ServerFacade.register("a", "a", "a");
        obj = ServerFacade.logout((Auth) obj);
        Assertions.assertEquals("{}", obj.toString());
    }

    @Test
    @Order(15)
    @DisplayName("Unsuccessful Logout")
    public void logoutF() {
        ServerFacade.register("a", "a", "a");
        Object obj = ServerFacade.logout(new Auth("", "")); //invalid auth
        Assertions.assertNotEquals("{}", obj.toString());
    }

    private static Game createTestGame(int id) {
        return new Game(id, null, null, "test" + id, new ChessGame());
    }

}
