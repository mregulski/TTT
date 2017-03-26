package ppt.reshi.tictactoe;

/**
 * Created by Marcin Regulski on 26.03.2017.
 */

public enum CellType {
    NW(1), NE(2), SW(3), SE(4), N(5), W(6), E(7), S(8), C(9);

    private final int id;

    CellType(int id) {
        this.id = id;
    }

    public int getValue() {return id; }
}
