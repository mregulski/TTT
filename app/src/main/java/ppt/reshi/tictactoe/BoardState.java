package ppt.reshi.tictactoe;

import android.util.Log;

import java.util.Random;

/**
 * Created by Marcin Regulski on 26.03.2017.
 */

public class BoardState {
    private final static String TAG = "BoardState";
    private Field[][] fields;
    private boolean gameStarted = false;
    private Random rng;

    private Player playerOne;
    private Player playerTwo;

    private Player activePlayer;
    private OnEndListener endListener;

    public Field[][] getBoardCopy() {
        return fields.clone();
    }
    public BoardState(int size) {
        rng = new Random();
        fields = new Field[size][size];
        playerOne = new HumanPlayer();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                fields[x][y] =  new Field(x, y);
            }
        }
    }

    private OnMoveListener getOnMoveListener(final Player nextPlayer) {
        return new OnMoveListener() {
            @Override
            public void onMove(Field field) {
                Log.d(TAG, "move: " + field);
                field.setSymbol(activePlayer.getSymbol());
                Log.d(TAG, "new symbol: " + activePlayer.getSymbol());
                if (isGameWon(field)) {
                    end(true);
                } else if (!hasMovesLeft()) {
                    end(false);
                } else {
                    activePlayer = nextPlayer;
                    if (!activePlayer.isHuman()) {
                        activePlayer.move();
                    }
                }
            }
        };
    }

    public Symbol getActiveSymbol() {
        return activePlayer.getSymbol();
    }

    public Field getField(int x, int y) {
        if (x >= fields.length || y >= fields.length) {
            throw new IndexOutOfBoundsException("Field: (" + x + ", " + y + ") does not exist");
        }
        return fields[x][y];
    }

    // start the game
    public void start(boolean isOtherBot) {
        if (isOtherBot) {
            playerTwo = new BotPlayer(this);
        } else {
            playerTwo = new HumanPlayer();
        }
        boolean coinFlip = rng.nextBoolean();
        if (coinFlip) {
            playerOne.setSymbol(Symbol.X);
            playerTwo.setSymbol(Symbol.O);
            activePlayer = playerOne;
        } else {
            playerOne.setSymbol(Symbol.O);
            playerTwo.setSymbol(Symbol.X);
            activePlayer = playerTwo;
        }
        playerOne.setOnMoveListener(getOnMoveListener(playerTwo));
        playerTwo.setOnMoveListener(getOnMoveListener(playerOne));
        gameStarted = true;
        if (!activePlayer.isHuman()) {
            activePlayer.move();
        }
    }



    public boolean isGameWon(Field lastMove) {
        return checkRow(lastMove.getY())
                || checkColumn(lastMove.getX())
                || checkDiagonal(lastMove.getX(), lastMove.getY());
    }

    private boolean hasMovesLeft() {
        for (Field[] column : fields) {
            for (Field field : column) {
                if (field.getSymbol().equals(Symbol.EMPTY)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void end(boolean hasWinner) {
        gameStarted = false;
        if (hasWinner) {
            Log.d(TAG, "Game finished, winner:" + getActiveSymbol());
            endListener.onEnd(getActiveSymbol());
        } else {
            Log.d(TAG, "Draw.");
            endListener.onEnd(Symbol.EMPTY);
        }
    }

    /**
     * Perform a move for the clicked field
     * @param field field associated with clicked cell
     * @return field's updated symbol
     */
    public void requestMove(Field field) {
        if (activePlayer.isHuman()) {
            activePlayer.move(field);
        }
    }

    private boolean checkRow(int row) {
        Log.d(TAG, "Checking row: " + row);
        for (int col = 0; col < fields.length; col++) {
            Log.d(TAG, "Checking:" + getField(col, row));
            if (! getField(col, row).getSymbol().equals(activePlayer.getSymbol())) {
                Log.d(TAG, row + ": not a winning row");
                return false;
            }
        }
        Log.d(TAG, row + ": winning row");
        return true;
    }

    private boolean checkColumn(int column) {
        Log.d(TAG, "Checking column: " + column);
        for (int row = 0; row < fields.length; row++) {
            Log.d(TAG, "Checking:" + getField(column, row));
            if (! getField(column, row).getSymbol().equals(activePlayer.getSymbol())) {
                Log.d(TAG, column + ": not a winning column");
                return false;
            }
        }
        Log.d(TAG, column + ": winning column");
        return true;
    }

    private boolean checkDiagonal(int x, int y) {
        if (x == y) {
            for (int i = 0; i < fields.length; i++) {
                if (! getField(i, i).getSymbol().equals(activePlayer.getSymbol())) {
                    Log.d(TAG, "NW-SE: not a winning diagonal");
                    return false;
                }
            }
            Log.d(TAG, "NW-SE: winning diagonal");
            return true;
        }
        if (x + y == fields.length-1) {
            for (int i = 0; i < fields.length; i++) {
                if (! getField(i, fields.length - 1 - i).getSymbol().equals(activePlayer.getSymbol())) {
                    Log.d(TAG, "NE-SW: not a winning diagonal");
                    return false;
                }
            }
            Log.d(TAG, "NE-SW: winning diagonal");
            return true;
        }
        return false;
    }

    public boolean isBoardClickable() {
        return gameStarted && activePlayer != null && activePlayer.isHuman();
    }

    public void setOnEndListener(OnEndListener endListener) {
        this.endListener = endListener;
    }

    public int getSize() {
        return fields.length;
    }
}
