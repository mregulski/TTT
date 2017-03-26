package ppt.reshi.tictactoe;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;


public class GameActivity extends AppCompatActivity {

    private final static String TAG = "GameActivity";
    private GridLayout mBoard;
    private BoardState mState;
    private Button mBotButton;
    private Button mHumanButton;
    private int mSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mSize = 4; // TODO: configurable
        createBoard();
        mBotButton = (Button) findViewById(R.id.btn_start_bot);
        mHumanButton = (Button) findViewById(R.id.btn_start_human);
        mBotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearBoard();
                mState.start(true);
                mBotButton.setVisibility(View.GONE);
                mHumanButton.setVisibility(View.GONE);
            }
        });
        mHumanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearBoard();
                mState.start(false);
                mBotButton.setVisibility(View.GONE);
                mHumanButton.setVisibility(View.GONE);
            }
        });
        mState.setOnEndListener(new OnEndListener() {
            @Override
            public void onEnd(Symbol winner) {
                if (Symbol.EMPTY.equals(winner)) {
                    Toast.makeText(getContext(), "Draw!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Winner: " + winner, Toast.LENGTH_SHORT).show();
                }
                mBotButton.setVisibility(View.VISIBLE);
                mHumanButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private Context getContext() {
        return this;
    }
    private void createBoard() {
        mState = new BoardState(mSize);
        mBoard = (GridLayout) findViewById(R.id.gl_board);
        mBoard.setRowCount(mSize);
        mBoard.setColumnCount(mSize);
        for (int y = 0; y < mSize; y++) {
            for (int x = 0; x < mSize; x++)
            createField(x, y);
        }
    }

    private void updateCellDrawable(ImageView cell, Symbol newSymbol) {
        ((LayerDrawable) cell.getDrawable()).getDrawable(1).setLevel(newSymbol.getValue());
    }

    private void clearBoard() {
        for (int i = 0; i < mBoard.getChildCount(); i++) {
            ImageView cell = (ImageView) mBoard.getChildAt(i);
            Field tag = (Field) cell.getTag();
            tag.setSymbol(Symbol.EMPTY);
            updateCellDrawable(cell, tag.getSymbol());

        }
    }

    private View createField(int x, int y) {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        ImageView cell = new ImageView(this);
        cell.setImageDrawable(getFieldDrawable(x,y));
        cell.setOnClickListener(new FieldClickListener());
        Field field = mState.getField(x, y);
        field.setCell(cell);
        cell.setTag(field);

        mBoard.addView(cell, params);
        return cell;
    }

    private Drawable getFieldDrawable(int x, int y) {
        LayerDrawable drawable = (LayerDrawable) getDrawable(R.drawable.field);
        drawable.getDrawable(0).setLevel(getFieldType(x,y).getValue());
        drawable.getDrawable(1).setLevel(0);
        return drawable;
    }

    private CellType getFieldType(int x, int y) {
        if (x == 0) {
            if (y == 0) {
                return CellType.NW;
            }
            if (y == mSize-1) {
                return CellType.SW;
            }
            return CellType.W;
        }
        if (x == mSize-1) {
            if (y == 0) {
                return CellType.NE;
            }
            if (y == mSize-1) {
                return CellType.SE;
            }
            return CellType.E;
        }
        if (y == 0) {
            return CellType.N;
        }
        if (y == mSize - 1) {
            return CellType.S;
        }
        return CellType.C;
    }


    private class FieldClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            LayerDrawable img = ((LayerDrawable)((ImageView) v).getDrawable());
            Field field = (Field) v.getTag();
            if (field.getSymbol().equals(Symbol.EMPTY) && mState.isBoardClickable()) {
                updateCellDrawable((ImageView) v, ((Field) v.getTag()).getSymbol());
                mState.requestMove(field);
            }
        }
    }
}
