package ppt.reshi.tictactoe;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Marcin Regulski on 26.03.2017.
 */

class BotPlayer implements Player {
    private final static String TAG = "BotPlayer";
    private final static int DEPTH_LIMIT = 2;
    private Symbol symbol;
    private OnMoveListener listener;
    final private BoardState board;
    final private Random rng;

    public BotPlayer(BoardState board) {
        this.board = board;
        this.rng = new Random();
    }


    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public Symbol getSymbol() {
        return symbol;
    }


    @Override
    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    public void setOnMoveListener(OnMoveListener listener) {
        this.listener = listener;
    }

    @Override
    public void move(Field field) {
        throw new RuntimeException("move(Field) can only be called on human players");
    }

    @Override
    public void move() {
        // evaluate field and pick the best one
        Field field = findBestMove();
        listener.onMove(field);
    }


    private Field findBestMove() {
        Field[][] currentState = board.getBoardCopy();
        Field best = null;
        int bestValue = -10000;
        List<Integer> xs = new ArrayList<>();
        List<Integer> ys = new ArrayList<>();
        for (int i = 0; i < currentState.length; i++) {
            xs.add(i);
            ys.add(i);
        }
        Collections.shuffle(xs);
        Collections.shuffle(ys);
        for (Integer x : xs)  {
            for (Integer y : ys) {
                if (currentState[x][y].getSymbol().equals(Symbol.EMPTY)) {
                    currentState[x][y].setSymbol(getSymbol());
                    int value = minimax(currentState, 0, getSymbol().equals(Symbol.X));
                    currentState[x][y].setSymbol(Symbol.EMPTY);
                    if (value > bestValue) {
                        bestValue = value;
                        best = board.getField(x, y);
                    }
                }
            }
        }
        Log.d(TAG, "bot moving to (" + best.getX() + "," +best.getY() + "), value: " + bestValue);
        return best;
//        int x, y;
//        do {
//            x = rng.nextInt(board.getSize());
//            y = rng.nextInt(board.getSize());
//        } while(!board.getField(x,y).getSymbol().equals(Symbol.EMPTY));
//
//        return board.getField(x, y);
    }

    private boolean hasMovesLeft(Field[][] fields) {
        for (Field[] column : fields) {
            for (Field field : column) {
                if (field.getSymbol().equals(Symbol.EMPTY)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @param board
     * @param depth
     * @param isMaximizer true, if this bot plays X's
     * @return
     */
    private int minimax(Field[][] board, int depth, boolean isMaximizer) {
        Log.d(TAG, "minimax: depth " + depth);
        if (depth >= DEPTH_LIMIT) {
            return 0;
        }
        int score = evaluate(board);
        if (score == 100 || score == -100) {
            return score;
        }
        if (!hasMovesLeft(board)) {
            return 0;
        }

        if (isMaximizer) {
            int best = -1000;
            for (Field[] column : board) {
                for (Field field : column) {
                    if (field.getSymbol().equals(Symbol.EMPTY)) {
                        field.setSymbol(getSymbol());
                        best = Math.max(best, minimax(board, depth + 1, !isMaximizer));
                        field.setSymbol(Symbol.EMPTY);
                    }
                }
            }
            return best;
        } else {
            int best = 1000;
            for (Field[] column : board) {
                for (Field field : column) {
                    if (field.getSymbol().equals(Symbol.EMPTY)) {
                        field.setSymbol(getOpponentSymbol());
                        best = Math.min(best, minimax(board, depth+1, !isMaximizer));
                        field.setSymbol(Symbol.EMPTY);
                    }
                }
            }
            return best;
        }
    }

    private int evaluate(Field[][] board) {
        if (checkDiagonalNESW(board)) {
            return getScore(board[0][board.length-1].getSymbol());
        }
        if (checkDiagonalNWSE(board)) {
            return getScore(board[0][0].getSymbol());
        }
        for (int i = 0; i < board.length; i++) {
            if (checkRow(i, board)) {
                return getScore(board[0][i].getSymbol());
            }
            if (checkCol(i, board)) {
                return getScore(board[i][0].getSymbol());
            }
        }

        return 0;
    }

    private boolean checkRow(int row, Field[][] board) {
        Log.d(TAG, "Checking row: " + row);
        Symbol first = board[0][row].getSymbol();
        for (int col = 0; col < board.length; col++) {
            Log.d(TAG, "Checking:" + board[col][row]);
            if (! board[col][row].getSymbol().equals(first)) {
                Log.d(TAG, row + ": not a winning row");
                return false;
            }
        }
        Log.d(TAG, row + ": winning row");
        return true;
    }

    private boolean checkCol(int col, Field[][] board) {
        Log.d(TAG, "Checking row: " + col);
        Symbol first = board[col][0].getSymbol();
        for (int row = 0; row < board.length; row++) {
            Log.d(TAG, "Checking:" + board[col][row]);
            if (! board[col][row].getSymbol().equals(first)) {
                Log.d(TAG, col + ": not a winning col");
                return false;
            }
        }
        Log.d(TAG, col + ": winning col");
        return true;
    }

    private boolean checkDiagonalNWSE(Field[][] board) {
        Symbol first = board[0][0].getSymbol();
        for (int i = 0; i < board.length; i++) {
            if (! board[i][i].getSymbol().equals(first)) {
                Log.d(TAG, "NW-SE: not a winning diagonal");
                return false;
            }
        }
        Log.d(TAG, "NW-SE: winning diagonal");
        return true;
    }
    private boolean checkDiagonalNESW( Field[][] board) {
        Symbol first = board[0][board.length-1].getSymbol();
        for (int i = 0; i < board.length; i++) {
            if (! board[i][board.length - 1 - i].getSymbol().equals(first)) {
                Log.d(TAG, "NE-SW: not a winning diagonal");
                return false;
            }
        }
        Log.d(TAG, "NE-SW: winning diagonal");
        return true;
    }

    private int getScore(Symbol first) {
        if (first.equals(getSymbol())) {
            Log.d(TAG, "evaluate: +100");
            return 100;
        } else if (first.equals(getOpponentSymbol())) {
            Log.d(TAG, "evaluate: -100");
            return -100;
        } else { //found four empties
            Log.d(TAG, "evaluate: 0");
            return 0;
        }
    }


    private boolean isMovesLeft(Field[][] board) {
        for (Field[] column : board) {
            for (Field field : column) {
                if (field.getSymbol().equals(Symbol.EMPTY)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Symbol getOpponentSymbol() {
        return getSymbol().equals(Symbol.X) ? Symbol.O : Symbol.X;
    }
}
