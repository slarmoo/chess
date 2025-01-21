package chess;

import java.util.Collection;
import java.lang.Math;

/**
 * super class for calculating chess moves
 */
public class ChessCalculator {

    public ChessCalculator(ChessBoard board, ChessPiece piece) {
        this.board = board;
        this.piece = piece;
    }

    private final ChessBoard board;
    private final ChessPiece piece;

    public boolean calculateKing(ChessMove move) {
        if(piece.getPieceType() == ChessPiece.PieceType.KING) {
            int startRow = move.getStartPosition().getRow();
            int startColumn = move.getStartPosition().getColumn();
            int endRow = move.getEndPosition().getRow();
            int endColumn = move.getEndPosition().getColumn();
            if(board.getPiece(move.getStartPosition()) == piece && board.getPiece(move.getEndPosition()) == null && move.getStartPosition() != move.getEndPosition()) {
                return Math.abs(startRow - endRow) < 1 && Math.abs(startColumn - endColumn) < 1;
            }
        }
        return false;
    }

    public boolean calculateRook(ChessMove move) {
        if(piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            int startRow = move.getStartPosition().getRow();
            int startColumn = move.getStartPosition().getColumn();
            int endRow = move.getEndPosition().getRow();
            int endColumn = move.getEndPosition().getColumn();
            if(board.getPiece(move.getStartPosition()) == piece && board.getPiece(move.getEndPosition()) == null && move.getStartPosition() != move.getEndPosition()) {
                return startRow == endRow || startColumn == endColumn;
            }
        }
        return false;
    }

    public boolean calculateBishop(ChessMove move) {
        if(piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            int startRow = move.getStartPosition().getRow();
            int startColumn = move.getStartPosition().getColumn();
            int endRow = move.getEndPosition().getRow();
            int endColumn = move.getEndPosition().getColumn();
            if(board.getPiece(move.getStartPosition()) == piece && board.getPiece(move.getEndPosition()) == null && move.getStartPosition() != move.getEndPosition()) {
                return Math.abs(startRow - endRow) == Math.abs(startColumn - endColumn);
            }
        }
        return false;
    }

    public boolean calculateQueen(ChessMove move) {
        if(piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            int startRow = move.getStartPosition().getRow();
            int startColumn = move.getStartPosition().getColumn();
            int endRow = move.getEndPosition().getRow();
            int endColumn = move.getEndPosition().getColumn();
            if(board.getPiece(move.getStartPosition()) == piece && board.getPiece(move.getEndPosition()) == null && move.getStartPosition() != move.getEndPosition()) {
                return Math.abs(startRow - endRow) == Math.abs(startColumn - endColumn) || startRow == endRow || startColumn == endColumn;
            }
        }
        return false;
    }

    public boolean calculateKnight(ChessMove move) {
        if(piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            int startRow = move.getStartPosition().getRow();
            int startColumn = move.getStartPosition().getColumn();
            int endRow = move.getEndPosition().getRow();
            int endColumn = move.getEndPosition().getColumn();
            if(board.getPiece(move.getStartPosition()) == piece && board.getPiece(move.getEndPosition()) == null && move.getStartPosition() != move.getEndPosition()) {
                return Math.abs(startRow - endRow) == Math.abs(startColumn - endColumn) || startRow == endRow || startColumn == endColumn;
            }
        }
        return false;
    }


}