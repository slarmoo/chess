package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    @Override
    public int hashCode() {
        return startPosition.hashCode() + endPosition.hashCode()*64 + (promotionPiece==null?0:promotionPiece.hashCode())*64*64;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ChessMove other) {
            return startPosition.equals(other.startPosition) && endPosition.equals(other.endPosition) && promotionPiece == other.promotionPiece;
        }
        return false;
    }

    @Override
    public String toString() {
        return "ChessMove{" +
                "start=" + startPosition +
                ", end=" + endPosition +
                ", promotionPiece=" + promotionPiece +
                '}' + "\n";
    }
}
