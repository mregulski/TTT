package ppt.reshi.tictactoe;

/**
 * Created by Marcin Regulski on 26.03.2017.
 */

interface Player {
    boolean isHuman();
    void move();

    void setOnMoveListener(OnMoveListener listener);

    Symbol getSymbol();

    void move(Field field);

    void setSymbol(Symbol x);
}
