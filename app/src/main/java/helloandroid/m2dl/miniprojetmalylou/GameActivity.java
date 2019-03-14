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
    private ArrayList<Integer> valuesToLaunch = new ArrayList<>();
    private Integer gameScore = 0;
    private int cptTouch = 1;
    private boolean gameStarted = false;

    // colonnes
    private int[] colonnes = new int[3];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        initDisplayer();
        resizeLayout();

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

        valuesToLaunch.addAll(values);

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
        SurfaceView sv = findViewById(R.id.gameSurface);
        display = new GameDisplay(size, sv);

        colonnes[0] = size.x /4;
        colonnes[1] = size.x /2;
        colonnes[2] = 3*size.x /4;
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
        display.setCptTouch(cptTouch++);
        Point point = new Point((int) motionEvent.getX(), (int) motionEvent.getY());
        gameScore += display.getScoreFromTouchedPosition(point);
        updateScoreDisplay();
        return false;
    }

    /**
     * ralentit le process pour l'affichage
     */
    private void waitAndDraw(int waitUntilNext, int modulo) {
        double next_tick = System.currentTimeMillis();
        int loops = 0;
        while (System.currentTimeMillis() > next_tick
                && loops < MAX_FRAMESKIP) {

            next_tick += SKIP_TICKS;
            loops++;
        }

        if (!valuesToLaunch.isEmpty() &&  waitUntilNext % modulo == 0) {
            // ajout d'une boule au displayer
            display.addPointOnDisplay(new Point(getRandomColumn(),20), valuesToLaunch.remove(0));
        }

        // fait descendre d'un cran les boules et vérifie qu'il y en ait ou non qui sont arrivées en bas
        int scoreDown = display.update();
        gameScore -= scoreDown;

        // màj affichage
        display.draw();
        updateScoreDisplay();
    }

    private int getRandomColumn() {
        int col = (int) (Math.random() * 100) % 3;

        return colonnes[col];
    }

    private void updateScoreDisplay() {
        TextView t = findViewById(R.id.scoreText);
        //t.setText(SCORE + gameScore);
    }

    private Thread thr = new Thread(new Runnable() {
        @Override
        public void run() {
            resizeLayout();
            int waitUntilNext = 0;
            int modulo = 52;

            while (gameStarted) {

                waitAndDraw(waitUntilNext, modulo);
                waitUntilNext = (waitUntilNext + 1) % modulo;
                if (display.allValuesLaunched()) {
                    Button b = findViewById(R.id.buttonStopGame);
       //             b.setText("START");

                    gameStarted = false;
                }
            }

        }
    });

    public void gameStartStop(View view) {
        Button b = findViewById(R.id.buttonStopGame);

        if (!gameStarted) {
            b.setText("STOP");
            gameStarted = true;
            thr.start();//.run();
        } else {
            b.setText("START");
            gameStarted = false;
            thr.stop();
        }
    }
}
