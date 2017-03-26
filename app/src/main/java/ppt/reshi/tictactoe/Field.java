package ppt.reshi.tictactoe;

import android.graphics.drawable.LayerDrawable;
import android.widget.ImageView;

/**
 * Created by Marcin Regulski on 26.03.2017.
 */

public class Field implements Cloneable {
    private final int x;
    private final int y;
    private Symbol symbol;

    private ImageView cell;

    public Field(int x, int y) {
        this.x = x;
        this.y = y;
        this.symbol = Symbol.EMPTY;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public boolean canClick() {
        return symbol.equals(Symbol.EMPTY);
    }

    @Override
    public String toString() {
        return "(" + getX() + ", " + getY() + ", " + getSymbol() + ")";
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
        ((LayerDrawable) cell.getDrawable()).getDrawable(1).setLevel(symbol.getValue());
    }

    public void setCell(ImageView cell) {
        this.cell = cell;
    }
}
