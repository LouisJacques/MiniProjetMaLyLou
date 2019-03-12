package helloandroid.m2dl.miniprojetmalylou;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import helloandroid.m2dl.miniprojetmalylou.diplay.GameDisplay;

public class GameActivity extends AppCompatActivity implements View.OnTouchListener {

    private GameDisplay display;

    // consants
    private static final String SCORE = "SCORE : ";
    private static final int TICKS_PER_SECOND = 25;
    private static final int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
    private static final int MAX_FRAMESKIP = 5;

    private ArrayList<Integer> values = new ArrayList<>();
    private Integer gameScore = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        resizeLayout();
        initDisplayer();

        View view = findViewById(R.id.gameLayout);
        view.setOnTouchListener(this);

        if(getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            String res = "";
            if (bundle.getIntegerArrayList("valuesLight") != null) {
                values.addAll(bundle.getIntegerArrayList("valuesLight"));
            }
            if (bundle.getIntegerArrayList("valuesGPS") != null) {
                values.addAll(bundle.getIntegerArrayList("valuesGPS"));
            }
            if (bundle.getIntegerArrayList("valuesTouch") != null) {
                values.addAll(bundle.getIntegerArrayList("valuesTouch"));
            }
            if (bundle.getIntegerArrayList("valuesAcc") != null) {
                values.addAll(bundle.getIntegerArrayList("valuesAcc"));
            }
            if (bundle.getIntegerArrayList("valuesSound") != null) {
                values.addAll(bundle.getIntegerArrayList("valuesSound"));
            }
        }

        /*Thread thr = new Thread(new Runnable() {
            @Override
            public void run() {
                double next_tick = System.currentTimeMillis();
                int loops = 0;
                while (System.currentTimeMillis() > next_tick
                        && loops < MAX_FRAMESKIP/2) {


                    next_tick += SKIP_TICKS;
                    loops++;
                }
                resizeLayout();
            }
        });*/

    }

    public void setGameValues(ArrayList<Integer> values) {
        this.values = values;
    }

    private void initDisplayer() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        display = new GameDisplay(size, (SurfaceView) findViewById(R.id.surfaceView));
    }

    private void resizeLayout() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);

        LinearLayout l = findViewById(R.id.gameAllLayout);
        LinearLayout l2 = findViewById(R.id.gameBarLayout);
        SurfaceView sv = findViewById(R.id.gameSurface);

        ViewGroup.LayoutParams params = l.getLayoutParams();
        params.height = size.y;
        params.width = size.x;

        ViewGroup.LayoutParams params2 = l2.getLayoutParams();
        params2.height = size.y;
        params2.width = size.x;

        sv.setMinimumHeight(l.getHeight()-61);
        sv.setMinimumWidth(l.getWidth()-2);

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Point point = new Point((int) motionEvent.getX(), (int) motionEvent.getY());
        return false;
    }
    /**
     * ralentit le process pour l'affichage
     */
    private void waitAndDraw() {
        double next_tick = System.currentTimeMillis();
        int loops = 0;
        while (System.currentTimeMillis() > next_tick
                && loops < MAX_FRAMESKIP/2) {


            next_tick += SKIP_TICKS;
            loops++;
        }
        int scoreDown = display.update();

        gameScore -= scoreDown;
        updateScoreDisplay();
    }

    private void updateScoreDisplay() {
        TextView t = findViewById(R.id.scoreText);
        t.setText(SCORE + gameScore);
    }
}
