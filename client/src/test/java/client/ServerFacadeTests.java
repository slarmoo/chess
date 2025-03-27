package client;

import model.*;
import org.eclipse.jetty.io.ssl.ALPNProcessor;
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

}
