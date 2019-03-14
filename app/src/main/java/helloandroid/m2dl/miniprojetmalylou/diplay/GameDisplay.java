package helloandroid.m2dl.miniprojetmalylou.diplay;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.SurfaceView;

import java.util.HashMap;
import java.util.Map;

public class GameDisplay {
    protected SurfaceView sv;
    protected Point size;
    protected HashMap<Point, Integer> numbersValues = new HashMap<>();
    protected static final int RADIUS = 50;

    public int getCptTouch() {
        return cptTouch;
    }

    public void setCptTouch(int cptTouch) {
        this.cptTouch = cptTouch;
    }

    private  int cptTouch = 1;
    public GameDisplay(Point size, SurfaceView sv) {
        this.size = size;
        this.sv = sv;
    }

    public void addPointOnDisplay(Point p, Integer v) {
        numbersValues.put(p, v);
    }

    public SurfaceView getSurfaceView() {
        return sv;
    }

    /**
     * Fait descendre d'un cran les valeurs (supprimme ceux dépassant la limite)
     */
    public int update() {
        int scoreToDecrease = 0;
        HashMap<Point, Integer> copy = copyMap(numbersValues);

        for (Point p: copy.keySet()) {
            int v = copy.get(p);
            numbersValues.remove(p);
            if (p.y >= sv.getHeight()) {
                scoreToDecrease += v;
            } else {
                numbersValues.put(new Point(p.x, p.y + cptTouch ), v);
            }
        }

        return scoreToDecrease;
    }

    private HashMap<Point, Integer> copyMap(HashMap<Point, Integer> map) {
        HashMap<Point, Integer> temp = new HashMap<>();
        for (Map.Entry<Point, Integer> entry: map.entrySet()) {
            temp.put(entry.getKey(), entry.getValue());
        }

        return temp;
    }

    /**
     * à partir du point de l'écran touché, màj la valeur éventuellement touchée et donne un point
     * (0 si rien de touché)
     * @param touch
     * @return
     */
    public int getScoreFromTouchedPosition(Point touch) {
        Point found = null;
        Map<Point, Integer> temp = copyMap(numbersValues);
        for (Point p: temp.keySet()) {
            if ((int) distance(touch, p) <= RADIUS) {
                found = p;
                break;
            }
        }

        int ret = 0;

        if (found != null) {
            Integer curr = numbersValues.get(found);
            if (curr != null && curr <= 1) {
                // s'il restait 1 clic à faire alors on a +1 point
                numbersValues.remove(found);
                ret = 1;
            } else {
                // s'il restait 2+ clics alors on décrémente
                curr--;
                numbersValues.put(found, curr);
            }
        }

        update();

        return ret;
    }

    /**
     * Calcule la distance entre 2 points
     *
     * @param p1
     * @param p2
     * @return
     */
    private double distance(Point p1, Point p2) {
        double x1 = p1.x; double y1 = p1.y;
        double x2 = p2.x; double y2 = p2.y;
        return Math.sqrt(Math.pow((x1-x2), 2) + Math.pow((y1-y2), 2));
    }

    public void reset() {
        numbersValues.clear();
        draw();
    }

    /**
     * dessine le défilement des boules du jeu
     */
    public void draw() {
        Canvas c = sv.getHolder().lockCanvas();

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        c.drawRect(0,0,c.getWidth(),c.getHeight(), paint);

        Map<Point, Integer> nbTemp = copyMap(numbersValues);
        for (Map.Entry<Point, Integer> pt: nbTemp.entrySet()) {
            paint.setColor(getLevelColor(pt.getValue()));
            Point p = pt.getKey();
            c.drawCircle((float) p.x,(float)  p.y,(float)  RADIUS, paint);
        }

        sv.getHolder().unlockCanvasAndPost(c);
    }

    private int getLevelColor(Integer val) {
        int c = Color.RED;
        return c - (otherColor / val);
    }

    public boolean allValuesLaunched() {
        return numbersValues.isEmpty();
    }

    private int otherColor = 0x00ffff;
}
