package server;

import chess.ChessGame;
import org.junit.jupiter.api.*;
import passoff.model.*;
import passoff.server.TestServerFacade;
import server.Server;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import model.*;
import com.google.gson.Gson;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MyAPITests {
//    @AfterAll
//    static void stopServer() {
//        server.stop();
//    }
    private Server server;
    private static TestServerFacade serverFacade;
    private static TestUser existingUser;
    private static TestUser newUser;

    @BeforeAll
    public static void init() {
//        server = new Server();
//        var port = server.run(0);
//        System.out.println("Started test HTTP server on " + port);

        serverFacade = new TestServerFacade("localhost", Integer.toString(8082));

        existingUser = new TestUser("ExistingUser", "existingUserPassword", "eu@mail.com");
//
        newUser = new TestUser("NewUser", "newUserPassword", "nu@mail.com");
//
//        createRequest = new TestCreateRequest("testGame");
    }

    @BeforeEach
    public void setup() {
        server = new Server();
        server.run(8082);
    }

    @AfterEach
    public void cleanUp() {
        server.stop();
        server = null;
    }

    @Test
    @Order(1)
    @DisplayName("Using port")
    public void usingPort() {
        Assertions.assertEquals(8082, server.port());
    }

    @Test
    @Order(2)
    @DisplayName("Post User")
    public void postUser() {
        TestAuthResult loginResult = serverFacade.login(existingUser);

        assertHttpOk(loginResult);
        Assertions.assertEquals(existingUser.getUsername(), loginResult.getUsername(),
                "Response did not give the same username as user");
        Assertions.assertNotNull(loginResult.getAuthToken(), "Response did not return authentication String");
    }

    private void assertHttpOk(TestResult result) {
        Assertions.assertEquals(HttpURLConnection.HTTP_OK, serverFacade.getStatusCode(),
                "Server response code was not 200 OK (message: %s)".formatted(result.getMessage()));
        Assertions.assertFalse(result.getMessage() != null &&
                        result.getMessage().toLowerCase(Locale.ROOT).contains("error"),
                "Result returned an error message");
    }
}
