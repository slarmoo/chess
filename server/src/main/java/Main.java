import chess.*;
import dataaccess.DatabaseManager;
import server.Server;
import service.*;

public class Main {
    public static void main(String[] args) {
        ChessPiece piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
//        Service service = new UserService();
        Server server = new Server();
        server.run(8081);
        System.out.println(server.port());
    }
}