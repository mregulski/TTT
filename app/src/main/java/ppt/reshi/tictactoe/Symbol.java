package ppt.reshi.tictactoe;

/**
 * Created by Marcin Regulski on 26.03.2017.
 */

public enum Symbol {
    EMPTY(0), X(1), O(2);

    private final int value;

    Symbol(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
