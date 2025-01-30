package chess;

import java.util.Collection;
import java.util.Locale;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.pieceType = type;
    }

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType pieceType;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChessPiece other) {
            return (this.getTeamColor() == other.getTeamColor() && this.getPieceType() == other.getPieceType());
        }
        return false;
    }

    @Override
    public String toString() {

        String s = switch(pieceType) {
            case KING: {
                yield "k";
            }
            case QUEEN: {
                yield "q";
            }
            case BISHOP: {
                yield "b";
            }
            case KNIGHT: {
                yield "n";
            }
            case ROOK: {
                yield "r";
            }
            case PAWN: {
                yield "p";
            }
        };
        s = switch (pieceColor) {
            case WHITE: {
                yield s.toUpperCase(Locale.ROOT);
            }
            case BLACK: {
                yield s.toLowerCase(Locale.ROOT);
            }
        };
        return s;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessCalculator calc = new ChessCalculator(board, myPosition);
        return calc.calculateMoveSetForPiece();
    }
}
