import chess.*;
import ui.*;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN);
        System.out.println("♕ 240 Chess Client: " + piece);
        var board = new ChessBoard();
        board.resetBoard();
        ChessBoardUI.printBoard(board);
    }
}