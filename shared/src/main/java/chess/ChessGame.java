package chess;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    public ChessGame() {
        this.teamColor = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();
    }

    private TeamColor teamColor;
    private ChessBoard board;

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        if (team == TeamColor.WHITE) {
            this.teamColor = TeamColor.BLACK;
        } else {
            this.teamColor = TeamColor.WHITE;
        }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessCalculator calc = new ChessCalculator(this.board, startPosition);
        return calc.calculateMoveSetForPiece();
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
//        Collection<ChessMove> moveSet = validMoves(move.getStartPosition());
//        boolean validMove = false;
//        while(moveSet.iterator().hasNext()) {
//            ChessMove nextMove = moveSet.iterator().next();
//            if(move.equals(nextMove)) {
//                validMove = true;
//            }
//        }
//        if(!validMove) {
//            throw new InvalidMoveException();
//        } else {
            board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
            board.addPiece(move.getStartPosition(), null);
            setTeamTurn(this.teamColor);
//        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor opponentColor = teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        Collection<ChessMove> opponentMoveSet = getAllValidMoves(opponentColor);
        ChessPosition kingPosition = findKingPosition(teamColor);
        Collection<ChessMove> kingMoveSet = validMoves(kingPosition);
        for(ChessMove move : opponentMoveSet) {
            if (move.getEndPosition().equals(kingPosition)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        TeamColor opponentColor = teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        Collection<ChessMove> opponentMoveSet = getAllValidMoves(opponentColor);
        ChessPosition kingPosition = findKingPosition(teamColor);
        Collection<ChessMove> kingMoveSet = validMoves(kingPosition);
        if(!isInCheck(teamColor)) { //must be in check to be in checkmate
            return false;
        }
        if(kingMoveSet == null) { //if in check and king cannot move must be in checkmate
            //TODO: unless you can capture piece that is causing king to be in check
            return true;
        }
        for(ChessMove kingMove : kingMoveSet) {
            boolean moveLeadsToCheck = false;
            for(ChessMove opponentMove : opponentMoveSet) {
                if(opponentMove.getEndPosition().equals(kingMove.getEndPosition())) {
                    moveLeadsToCheck = true;
                    break;
                }
            }
            if(!moveLeadsToCheck) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        Collection<ChessMove> moveSet = getAllValidMoves(teamColor);
        return moveSet.isEmpty();
    }

    private Collection<ChessMove> getAllValidMoves(TeamColor teamColor) {
        var moveSet = new ArrayList<ChessMove>();
        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
                ChessPiece piece = this.board.getPiece(new ChessPosition(i, j));
                if(piece != null && piece.getTeamColor() == teamColor) {
                    moveSet.addAll(validMoves(new ChessPosition(i, j)));
                }
            }
        }
        return moveSet;
    }

    private  ChessPosition findKingPosition(TeamColor teamColor) {
//        System.out.println(teamColor);
//        System.out.println(this.board);
        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
                ChessPiece piece = this.board.getPiece(new ChessPosition(i, j));
//                System.out.println(piece);
                if(piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return new ChessPosition(i, j);
                }
            }
        }
        //There should always be a king on board
        return null;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
