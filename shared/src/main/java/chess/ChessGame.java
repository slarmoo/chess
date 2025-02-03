package chess;

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
        this.teamColor = team;
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
        Collection<ChessMove> moveSet = validMoves(move.getStartPosition());
        boolean validMove = false;
        if(moveSet == null || this.board.getPiece(move.getStartPosition()).getTeamColor() != this.getTeamTurn()) {
            throw new InvalidMoveException();
        }
        for(ChessMove nextMove : moveSet) {
            //make future game step
            ChessGame futureGame = new ChessGame();
            ChessBoard futureBoard = new ChessBoard();
            futureBoard.copyBoard(this.board);
            futureGame.setBoard(futureBoard);
            //do move
            futureGame.makeMoveUnhandled(move);
            //is still in check?
            if (move.equals(nextMove) && !futureGame.isInCheck(teamColor)) {
                validMove = true;
                break;
            }
        }
        if(!validMove) {
            throw new InvalidMoveException();
        } else {
            board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
            board.addPiece(move.getStartPosition(), null);
            setTeamTurn((this.teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE);
        }
    }

    public void makeMoveUnhandled(ChessMove move) {
        board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
        board.addPiece(move.getStartPosition(), null);
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
//        Collection<ChessMove> kingMoveSet = validMoves(kingPosition);
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
//        TeamColor opponentColor = teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
//        Collection<ChessMove> opponentMoveSet = getAllValidMoves(opponentColor);
//        ChessPosition kingPosition = findKingPosition(teamColor);
//        Collection<ChessMove> kingMoveSet = validMoves(kingPosition);
        if(!isInCheck(teamColor)) { //must be in check to be in checkmate
            return false;
        }
        Collection<ChessMove> selfMoveSet = getAllValidMoves(teamColor);
        for(ChessMove move : selfMoveSet) {
            //make future game step
            ChessGame futureGame = new ChessGame();
            ChessBoard futureBoard = new ChessBoard();
            futureBoard.copyBoard(this.board);
            futureGame.setBoard(futureBoard);
            //do move
            futureGame.makeMoveUnhandled(move);
            //is still in check?
            if(!futureGame.isInCheck(teamColor)) {
                return false;
            }
        }
        return true;
//        if(kingMoveSet == null) { //if in check and king cannot move must be in checkmate
//            //TODO: unless you can capture piece that is causing king to be in check / move in the way
//            return true;
//        } //else if()
//        for(ChessMove kingMove : kingMoveSet) {
//            boolean moveLeadsToCheck = false;
//            for(ChessMove opponentMove : opponentMoveSet) {
//                if(opponentMove.getEndPosition().equals(kingMove.getEndPosition())) {
//                    moveLeadsToCheck = true;
//                    break;
//                }
//            }
//            if(!moveLeadsToCheck) {
//                return true;
//            }
//        }
//        return false;
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
        if(moveSet.isEmpty()) return true;
        TeamColor opponentColor = teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        Collection<ChessMove> opponentMoveSet = getAllValidMoves(opponentColor);
        ChessPosition kingPosition = findKingPosition(teamColor);
        Collection<ChessMove> kingMoveSet = validMoves(kingPosition);
        if(isInCheck(teamColor)) { //must be in check to be in checkmate
            return false;
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
                return false;
            }
        }
        return true;
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
        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
                ChessPiece piece = this.board.getPiece(new ChessPosition(i, j));
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
