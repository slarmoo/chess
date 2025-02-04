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
        Collection<ChessMove> moveSet = calc.calculateMoveSetForPiece();
        Collection<ChessMove> filteredMoveSet = new ArrayList<>();
        if(moveSet == null) {
            return null;
        }
        TeamColor color = this.board.getPiece(startPosition).getTeamColor();
        for(ChessMove nextMove : moveSet) {
            //make future game step
            ChessGame futureGame = new ChessGame();
            ChessBoard futureBoard = new ChessBoard();
            futureBoard.copyBoard(this.board);
            futureGame.setBoard(futureBoard);
            //do move
            futureGame.makeMoveUnhandled(nextMove);
            //puts king in check?
            if (!futureGame.isInCheck(color)) {
                filteredMoveSet.add(nextMove);
            }
        }
        return filteredMoveSet;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> moveSet = validMoves(move.getStartPosition());
        if(moveSet == null || this.board.getPiece(move.getStartPosition()).getTeamColor() != this.getTeamTurn()) {
            throw new InvalidMoveException();
        }
        boolean validMove = false;
        for(ChessMove curMove : moveSet) {
            if(curMove.equals(move)) {
                validMove = true;
                break;
            }
        }
        if(validMove) {
            makeMoveUnhandled(move);
            setTeamTurn((this.teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE);
        } else {
            throw new InvalidMoveException();
        }

    }

    public void makeMoveUnhandled(ChessMove move) {
        if(move.getPromotionPiece() != null) {
            ChessPiece prom = new ChessPiece(board.getPiece(move.getStartPosition()).getTeamColor(), move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), prom);
        } else {
            board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
        }
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
        if(!isInCheck(teamColor)) { //must be in check to be in checkmate
            return false;
        }
        Collection<ChessMove> selfMoveSet = getAllValidMoves(teamColor);
        return predictFuture(selfMoveSet);
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
        if(moveSet.isEmpty()) {
            return true;
        }
        if(isInCheck(teamColor)) { //must not be in check to be in stalemate
            return false;
        }
        Collection<ChessMove> selfMoveSet = getAllValidMoves(teamColor);
        return predictFuture(selfMoveSet);
    }

    private boolean predictFuture(Collection<ChessMove> moveSet) {
        for(ChessMove move : moveSet) {
            //make future game step
            ChessGame futureGame = new ChessGame();
            ChessBoard futureBoard = new ChessBoard();
            futureBoard.copyBoard(this.board);
            futureGame.setBoard(futureBoard);
            //do move
            futureGame.makeMoveUnhandled(move);
            //is still in check?
            if(!futureGame.isInCheck(teamColor)) { //can make a safe move
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
                    ChessCalculator calc = new ChessCalculator(this.board, new ChessPosition(i, j));
                    moveSet.addAll(calc.calculateMoveSetForPiece());
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
