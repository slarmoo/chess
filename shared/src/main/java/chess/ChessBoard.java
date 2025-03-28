package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    public ChessBoard() {
        //this.resetBoard();
    }

    public void copyBoard(ChessBoard other) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                this.addPiece(pos, other.getPiece(pos));
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChessBoard other) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if(!(this.getPiece(new ChessPosition(i, j)) == null && other.getPiece(new ChessPosition(i, j)) == null)
                    && !this.getPiece(new ChessPosition(i, j)).equals(other.getPiece(new ChessPosition(i, j)))) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public ChessPiece[][] chessBoardPieces = new ChessPiece[9][9];


    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        chessBoardPieces[position.getRow()][position.getColumn()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return chessBoardPieces[position.getRow()][position.getColumn()];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                chessBoardPieces[i][j] = null;
            }
        }
        ChessPiece[] pieceOrderBlack = {
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN),
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK),
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT),
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP),
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN),
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING),
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP),
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT),
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK)};
        ChessPiece[] pieceOrderWhite = {
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK),
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT),
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP),
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN),
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING),
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP),
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT),
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK)};
        for (int i = 1; i < pieceOrderBlack.length; i++) {
            addPiece(new ChessPosition(8,i), pieceOrderBlack[i]);
            addPiece(new ChessPosition(7,i), pieceOrderBlack[0]);
            addPiece(new ChessPosition(1,i), pieceOrderWhite[i]);
            addPiece(new ChessPosition(2,i), pieceOrderWhite[0]);
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int i = 8; i > 0; i--) {
            str.append("|");
            for (int j = 1; j < 9; j++) {
                str.append(chessBoardPieces[i][j] == null ? " |" : chessBoardPieces[i][j].toString() + "|");
            }
            str.append("\n");
        }
        return str.toString();
    }
}
