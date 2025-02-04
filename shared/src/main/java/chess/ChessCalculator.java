package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.lang.Math;

/**
 * super class for calculating chess moves
 */
public class ChessCalculator {

    public ChessCalculator(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.piece = board.getPiece(position);
        this.position = position;

    }

    private final ChessBoard board;
    private final ChessPiece piece;
    private final ChessPosition position;

    private final ChessPiece.PieceType[] promotionTypes = {
            ChessPiece.PieceType.BISHOP,
            ChessPiece.PieceType.KNIGHT,
            ChessPiece.PieceType.QUEEN,
            ChessPiece.PieceType.ROOK,
    };

    private Collection<ChessMove> pawnPromotionCalculator(ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ArrayList<ChessMove> moveSet = new ArrayList<>();
        if(end.getRow() == (piece.getTeamColor() == ChessGame.TeamColor.BLACK ? 1 : 8)) {
            for (ChessPiece.PieceType promotionType : promotionTypes) {
                ChessMove pawnMove = new ChessMove(start, end, promotionType);
                moveSet.add(pawnMove);
            }
        } else {
            moveSet.add(move);
        }
        return moveSet;
    }

    public Collection<ChessMove> calculateMoveSetForPiece() {
        if(piece == null) {return null;}
        ArrayList<ChessMove> moveSet = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessMove move = new ChessMove(position, new ChessPosition(i,j), null);
                switch (piece.getPieceType()) {
                    case PAWN:
                        if(calculatePawn(move)) {
                            moveSet.addAll(pawnPromotionCalculator(move));
                        }
                        break;
                    case KING:
                        if(calculateKing(move)) {
                            moveSet.add(move);
                        }
                        break;
                    case QUEEN:
                        if(calculateQueen(move)) {
                            moveSet.add(move);
                        }
                        break;
                    case KNIGHT:
                        if(calculateKnight(move)) {
                            moveSet.add(move);
                        }
                        break;
                    case ROOK:
                        if(calculateRook(move)) {
                            moveSet.add(move);
                        }
                        break;
                    case BISHOP:
                        if(calculateBishop(move)) {
                            moveSet.add(move);
                        }
                        break;
                }
            }
        }
        return moveSet;
    }

    public boolean calculateKing(ChessMove move) {
        if(piece.getPieceType() == ChessPiece.PieceType.KING) {
            int startRow = move.getStartPosition().getRow();
            int startColumn = move.getStartPosition().getColumn();
            int endRow = move.getEndPosition().getRow();
            int endColumn = move.getEndPosition().getColumn();
            if(board.getPiece(move.getStartPosition()) == piece && (board.getPiece(move.getEndPosition()) == null
            || board.getPiece(move.getEndPosition()).getTeamColor() != piece.getTeamColor())
            && move.getStartPosition() != move.getEndPosition()) {
                return Math.abs(startRow - endRow) <= 1 && Math.abs(startColumn - endColumn) <= 1;
            }
        }
        return false;
    }

    public boolean calculateRook(ChessMove move) {
        if(piece.getPieceType() == ChessPiece.PieceType.ROOK || piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            int startRow = move.getStartPosition().getRow();
            int startColumn = move.getStartPosition().getColumn();
            int endRow = move.getEndPosition().getRow();
            int endColumn = move.getEndPosition().getColumn();
            if(board.getPiece(move.getStartPosition()) == piece && (board.getPiece(move.getEndPosition()) == null ||
            board.getPiece(move.getEndPosition()).getTeamColor() != piece.getTeamColor()) && move.getStartPosition() != move.getEndPosition()) {
                boolean rowBlocked = false;
                boolean colBlocked = false;
                for(int i = 1; i < 9; i++) {
                    if(board.getPiece(new ChessPosition(i, endColumn)) != null &&
                    ((startRow < i && i < endRow) || (startRow > i && i > endRow))) {
                        rowBlocked = true;
                    } else if(board.getPiece(new ChessPosition(endRow, i)) != null  &&
                    ((startColumn < i && i < endColumn) || (startColumn > i && i > endColumn))) {
                        colBlocked = true;
                    }
                }
                return (startRow == endRow && !colBlocked) || (startColumn == endColumn && !rowBlocked);
            }
        }
        return false;
    }

    private boolean calculateBishop(ChessMove move)  {
        int startRow = move.getStartPosition().getRow();
        int startColumn = move.getStartPosition().getColumn();
        int endRow = move.getEndPosition().getRow();
        int endColumn = move.getEndPosition().getColumn();
        boolean blockedNW = false;
        boolean blockedNE = false;
        boolean blockedSW = false;
        boolean blockedSE = false;
        for(int row = 1; row <= 8; row++) {
            for(int col = 1; col <= 8; col++) {
                ChessPiece otherPiece = board.getPiece(new ChessPosition(row, col));
                if(otherPiece != null && Math.abs(startRow - row) == Math.abs(startColumn - col)) {
                    if(startRow < row && row < endRow) {
                        if(startColumn < col && col < endColumn) {
                            blockedNW = true;
                        } else if(startColumn > col && col > endColumn) {
                            blockedNE = true;
                        }
                    } else if(startRow > row && row > endRow) {
                        if(startColumn < col && col < endColumn) {
                            blockedSW = true;
                        } else if(startColumn > col && col > endColumn) {
                            blockedSE = true;
                        }
                    }
                }
            }
        }
        if(!move.getStartPosition().equals(move.getEndPosition()) && (board.getPiece(move.getEndPosition()) == null
                || board.getPiece(move.getEndPosition()).getTeamColor() != piece.getTeamColor())) {
            if(Math.abs(startRow - endRow) == Math.abs(startColumn - endColumn)) {
                if(startRow < endRow && startColumn < endColumn && !blockedNW) {
                    return true;
                } else if (startRow < endRow && startColumn > endColumn && !blockedNE) {
                    return true;
                } else if (startRow > endRow && startColumn < endColumn && !blockedSW) {
                    return true;
                } else if (startRow > endRow && startColumn > endColumn && !blockedSE) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean calculateQueen(ChessMove move) {
        if(piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            return calculateBishop(move) || calculateRook(move);
        }
        return false;
    }

    public boolean calculateKnight(ChessMove move) {
        if(piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            int startRow = move.getStartPosition().getRow();
            int startColumn = move.getStartPosition().getColumn();
            int endRow = move.getEndPosition().getRow();
            int endColumn = move.getEndPosition().getColumn();
            if(board.getPiece(move.getStartPosition()) == piece && (board.getPiece(move.getEndPosition()) == null ||
            board.getPiece(move.getEndPosition()).getTeamColor() != piece.getTeamColor()) &&
            move.getStartPosition() != move.getEndPosition()) {
                return Math.abs(startRow - endRow) == 2 && Math.abs(startColumn - endColumn) == 1
                    || Math.abs(startRow - endRow) == 1 && Math.abs(startColumn - endColumn) == 2;
            }
        }
        return false;
    }

    public boolean calculatePawn(ChessMove move) {
        if(piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            int startRow = move.getStartPosition().getRow();
            int startColumn = move.getStartPosition().getColumn();
            int endRow = move.getEndPosition().getRow();
            int endColumn = move.getEndPosition().getColumn();
            boolean isAtStart = piece.getTeamColor() == ChessGame.TeamColor.BLACK ?
                (startRow == 7 && board.getPiece(new ChessPosition(startRow - 1, startColumn)) == null) :
                (startRow == 2 && board.getPiece(new ChessPosition(startRow + 1, startColumn)) == null);
            if(board.getPiece(move.getStartPosition()) == piece && move.getStartPosition() != move.getEndPosition()) {
                if(board.getPiece(move.getEndPosition()) == null && startColumn == endColumn) {
                    return startRow == endRow+(piece.getTeamColor() == ChessGame.TeamColor.BLACK ? 1 : -1) ||
                        (isAtStart && startRow == endRow+(piece.getTeamColor() == ChessGame.TeamColor.BLACK ? 2 : -2)) ;
                } else if(board.getPiece(move.getEndPosition()) != null &&
                board.getPiece(move.getEndPosition()).getTeamColor() != piece.getTeamColor()) {
                    return Math.abs(startColumn-endColumn) == 1 &&
                        startRow == endRow+(piece.getTeamColor() == ChessGame.TeamColor.BLACK ? 1 : -1);
                }
            }
        }
        return false;
    }
}