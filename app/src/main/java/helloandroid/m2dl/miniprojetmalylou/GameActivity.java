package helloandroid.m2dl.miniprojetmalylou;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
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

    // constants
    private static final String SCORE = "SCORE : ";
    private static final int TICKS_PER_SECOND = 25;
    private static final int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
    private static final int MAX_FRAMESKIP = 5;

    private ArrayList<Integer> values = new ArrayList<>();
    private ArrayList<Integer> valuesToLaunch = new ArrayList<>();
    private Integer gameScore = 0;
    private int cptTouch = 1;
    private boolean gameStarted = false;
    private boolean resized = false;

    private Button btnScore;
    private Button b;

    // colonnes
    private int[] colonnes = new int[3];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        btnScore = findViewById(R.id.scoreText);
        b = findViewById(R.id.buttonStopGame);

        initDisplayer();
        resizeLayout();

        View view = findViewById(R.id.gameLayout);
        view.setOnTouchListener(this);

        if(getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
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

        for (Integer v: values) {
            valuesToLaunch.add(v);
        }
    }

    /**
     * initialise GameDisplay
     */
    private void initDisplayer() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        SurfaceView sv = findViewById(R.id.gameSurface);
        display = new GameDisplay(size, sv);

        colonnes[0] = size.x /4;
        colonnes[1] = size.x /2;
        colonnes[2] = 3*size.x /4;
    }

    /**
     * redimentionne
     */
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

        sv.setMinimumHeight(l.getHeight()-80);
        sv.setMinimumWidth(l.getWidth()-2);

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (cptTouch < 10) {
            cptTouch++;
        }
        if (cptTouch % 2 == 0) {
            display.setCptTouch(cptTouch);
        }
        Point point = new Point((int) motionEvent.getX(), (int) motionEvent.getY() - (int) display.getSurfaceView().getY());
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
            int v = valuesToLaunch.remove(0) % 5;
            display.addPointOnDisplay(new Point(getRandomColumn(),25), v+1);
        }

        // fait descendre d'un cran les boules et vérifie qu'il y en ait ou non qui sont arrivées en bas
        int scoreDown = display.update();
        gameScore -= scoreDown;

        // màj affichage
        display.draw();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateScoreDisplay();
            }
        });

    }

    private int getRandomColumn() {
        int col = (int) (Math.random() * 100) % 3;
        return colonnes[col];
    }

    /**
     * màj le score sur l'affichage
     */
    private void updateScoreDisplay() {
        btnScore.setText(SCORE + gameScore);
    }

    private Thread thr = setUpThread();

    private Thread setUpThread() {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
            if (!resized) {
                resizeLayout();
                resized = true;
            }
            int waitUntilNext = 0;
            int modulo = 90/cptTouch;

            while (gameStarted) {

                waitAndDraw(waitUntilNext, modulo);
                waitUntilNext = (waitUntilNext + 1) % modulo;
                if (display.allValuesLaunched() && valuesToLaunch.isEmpty()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            b.setText("START");
                            gameStarted = false;
                            clearAndReinitValuesToLaunch();
                            dialogue(gameScore > 1);
                        }
                    });

                }
            }
            }
        });

        return th;
    }

    private void clearAndReinitValuesToLaunch() {
        valuesToLaunch.clear();
        for (Integer v: values) {
            valuesToLaunch.add(v);
        }
    }

    /**
     * Démarre/stoppe le jeu quand on clique sur le bouton START/STOP
     * @param view
     */
    public void gameStartStop(View view) {
        if (!gameStarted) {
            // màj bouton
            b.setText("STOP");
            gameStarted = true;

            // thread
            if (thr.getState() == Thread.State.TERMINATED) {
                thr = setUpThread();
            }
            if (thr.getState() == Thread.State.NEW) {
                thr.start();
            }
        } else {
            // màj bouton
            b.setText("START");
            gameStarted = false;

            // tread
            thr.interrupt();

             // reset affichage et valeurs
            display.reset();
            cptTouch = 1;
            clearAndReinitValuesToLaunch();

            dialogue(gameScore > 0);

            // score
            gameScore = 0;
        }
    }

    private void dialogue(boolean win) {
        Dialog d = new Dialog(this);
        d.setContentView(R.layout.popup);
        TextView txt = d.findViewById(R.id.dialogText);
        if (!win) {
            txt.setText("Félicitations, vous avez perdu!");
        } else {
            txt.setText("Vous avez fait disparaître toutes les boules!");
        }
        d.show();
    }
}
