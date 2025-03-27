package ui;

import chess.*;

public class ChessBoardUI {
    private static final String BLACK_BG_COLOR = EscapeSequences.SET_BG_COLOR_DARK_GREEN;
    private static final String WHITE_BG_COLOR = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
    private static final String BLACK_PIECE_COLOR = EscapeSequences.SET_TEXT_COLOR_BLACK;
    private static final String WHITE_PIECE_COLOR = EscapeSequences.SET_TEXT_COLOR_WHITE;
    private static final String EDGE_BG_COLOR = EscapeSequences.SET_BG_COLOR_DARK_GREY;
    private static final String EDGE_TEXT_COLOR = EscapeSequences.SET_TEXT_COLOR_WHITE;
    private static final String[] EDGE_LETTERS = {"h", "g", "f", "e", "d", "c", "b", "a"};

    public static void printBoard(ChessBoard board, boolean isRightSideUp) {
        printULBorder(isRightSideUp);
        if(isRightSideUp) {
            for(int i = 8; i >= 1; i--) {
                System.out.print(EDGE_BG_COLOR);
                System.out.print(EDGE_TEXT_COLOR);
                System.out.print(getPrettyCharacter(i + ""));
                for(int j = 1; j <= 8; j++) {
                    printPiece(i, j, board.getPiece(new ChessPosition(i, j)));
                }
                System.out.print(EDGE_BG_COLOR);
                System.out.print(EDGE_TEXT_COLOR);
                System.out.print(getPrettyCharacter(i + ""));
                System.out.print(EscapeSequences.RESET_BG_COLOR);
                System.out.print("\n");
            }
        } else {
            for(int i = 1; i <= 8; i++) {
                System.out.print(EDGE_BG_COLOR);
                System.out.print(EDGE_TEXT_COLOR);
                System.out.print(getPrettyCharacter(i + ""));
                for(int j = 8; j >= 1; j--) {
                    printPiece(i, j, board.getPiece(new ChessPosition(i, j)));
                }
                System.out.print(EDGE_BG_COLOR);
                System.out.print(EDGE_TEXT_COLOR);
                System.out.print(getPrettyCharacter(i + ""));
                System.out.print(EscapeSequences.RESET_BG_COLOR);
                System.out.print("\n");
            }
        }
        printULBorder(isRightSideUp);
    }

    private static void printPiece(int i, int j, ChessPiece piece) {
        if(piece != null && piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            System.out.print(BLACK_PIECE_COLOR);
        } else {
            System.out.print(WHITE_PIECE_COLOR);
        }
        if((i+j)%2==0) {
            System.out.print(BLACK_BG_COLOR);
        } else {
            System.out.print(WHITE_BG_COLOR);
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

    private static void printULBorder(boolean isRightSideUp) {
        System.out.print(EDGE_BG_COLOR);
        System.out.print(EDGE_TEXT_COLOR);
        System.out.print(getPrettyCharacter(" "));
        if(isRightSideUp) {
            for(int j = 7; j >= 0; j--) {
                System.out.print(getPrettyCharacter(EDGE_LETTERS[j]));
            }
        } else {
            for(int j = 0; j <= 7; j++) {
                System.out.print(getPrettyCharacter(EDGE_LETTERS[j]));
            }
        }
        System.out.print(getPrettyCharacter(" "));
        System.out.print(EscapeSequences.RESET_BG_COLOR);
        System.out.print("\n");
    }

    private static String getPrettyCharacter(String character) {
        return "\u2003" + character + " ";
    }
}
