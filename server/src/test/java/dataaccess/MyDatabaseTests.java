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

    private static SQLDAO sqldao = new SQLDAO();
//    private static UserDAO udao = new MemoryUserDAO(new Database());


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

}
