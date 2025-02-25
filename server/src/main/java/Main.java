import chess.*;
import server.Server;
import service.*;

public class Main {
    public static void main(String[] args) {
        ChessPiece piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        Service service = new UserService();
        Server server = new Server(service);
        server.run(8080);
    }
}