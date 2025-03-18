package ui;

import chess.*;

public class ChessBoardUI {
    private static final String blackBGColor = EscapeSequences.SET_BG_COLOR_DARK_GREEN;
    private static final String whiteBGColor = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
    private static final String blackPieceColor = EscapeSequences.SET_TEXT_COLOR_BLACK;
    private static final String whitePieceColor = EscapeSequences.SET_TEXT_COLOR_WHITE;

    public static void printBoard(ChessBoard board, boolean isUpsidedown) {
        final int boardStart = isUpsidedown ? 8 : 1;
        final int boardEnd = isUpsidedown ? 1 : 8;
        if(isUpsidedown) {
            for(int i = 8; i >= 1; i--) {
                for(int j = 8; j >= 1; j--) {
                    printPiece(i, j, board.getPiece(new ChessPosition(i, j)));
                }
                System.out.print(EscapeSequences.RESET_BG_COLOR);
                System.out.print("\n");
            }
        } else {
            for(int i = 1; i <= 8; i++) {
                for(int j = 1; j <= 8; j++) {
                    printPiece(i, j, board.getPiece(new ChessPosition(i, j)));
                }
                System.out.print(EscapeSequences.RESET_BG_COLOR);
                System.out.print("\n");
            }
        }
    }

    private static void printPiece(int i, int j, ChessPiece piece) {
        if(piece != null && piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            System.out.print(blackPieceColor);
        } else {
            System.out.print(whitePieceColor);
        }
        if((i+j)%2==0) {
            System.out.print(blackBGColor);
        } else {
            System.out.print(whiteBGColor);
        }
        System.out.print(getString(piece));
    }

    private static String getString(ChessPiece piece) {
        if(piece == null) {
            return EscapeSequences.EMPTY;
        }
        return switch (piece.getPieceType()) {
            case ROOK -> EscapeSequences.WHITE_ROOK;
            case KING -> EscapeSequences.WHITE_KING;
            case QUEEN -> EscapeSequences.WHITE_QUEEN;
            case KNIGHT -> EscapeSequences.WHITE_KNIGHT;
            case BISHOP -> EscapeSequences.WHITE_BISHOP;
            case PAWN -> EscapeSequences.WHITE_PAWN;
            case null -> EscapeSequences.EMPTY;
        };
    }
}
