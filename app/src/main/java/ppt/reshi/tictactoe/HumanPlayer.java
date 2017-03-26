package ppt.reshi.tictactoe;

/**
 * Created by Marcin Regulski on 26.03.2017.
 */

class HumanPlayer implements Player {
    private Symbol symbol;
    private OnMoveListener listener;

    @Override
    public boolean isHuman() {
        return true;
    }

    @Override
    public void move() {
        throw new UnsupportedOperationException("move() can only be called on bot players");
    }

    @Override
    public void move(Field field) {
        listener.onMove(field);
        // wait for click
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    @Override
    public Symbol getSymbol() {
        return symbol;
    }

    @Override
    public void setOnMoveListener(OnMoveListener listener) {
        this.listener = listener;
    }
}
