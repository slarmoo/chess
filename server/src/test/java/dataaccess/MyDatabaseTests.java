package dataaccess;

import chess.ChessGame;
import model.Auth;
import model.Game;
import model.User;
import org.junit.jupiter.api.*;
import service.Service;

import java.util.ArrayList;
import java.util.Collection;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MyDatabaseTests {

    private static final SQLDAO sqldao = new SQLDAO();
    private static final UserDAO userdao = new MemoryUserDAO(new Database());
    private static final User user1 = new User("user1", "pass1", "email1");
    private static final User user2 = new User("user2", "pass2", "email2");

    @BeforeAll
    public static void init() {


    }

    @BeforeEach
    public void setup() {
    }

    @AfterEach
    public void cleanUp() {
    }

    @Test
    @Order(1)
    @DisplayName("Database empty")
    public void checkDatabase() {
        Collection<User> users = sqldao.getAllUsers();
        Assertions.assertEquals(0, users.size());
    }

    @Test
    @Order(2)
    @DisplayName("Add user")
    public void addUser() {
        try {
            sqldao.addUserSQL(user1, userdao.createAuth(user1));
            sqldao.addUserSQL(user2, userdao.createAuth(user2));
            Collection<User> users = sqldao.getAllUsers();
            Assertions.assertEquals(2, users.size());
            Assertions.assertTrue(users.contains(user1));
            Assertions.assertTrue(users.contains(user2));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

}
