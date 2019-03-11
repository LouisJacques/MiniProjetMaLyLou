package helloandroid.m2dl.miniprojetmalylou.diplay;

import android.graphics.Point;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.HashMap;

public class GameDisplay {
    private SurfaceView sv;
    private Point size;
    private HashMap<Point, Integer> numbersValues = new HashMap<>();
    private static final int RADIUS = 20;

    public GameDisplay(Point size, SurfaceView sv) {
        this.size = size;
        this.sv = sv;
    }

    /**
     * Fait descendre d'un cran les valeurs
     */
    public int update() {
        int scoreToDecrease = 0;
        ArrayList<Point> donePoints = new ArrayList<>();
        for (Point p: numbersValues.keySet()) {
            p.set(p.x, p.y + 1);
            if (p.y >= sv.getHeight()) {
                donePoints.add(p);
            }
        }

        for (Point p: donePoints) {
            scoreToDecrease += numbersValues.get(p);
            numbersValues.remove(p);
        }

        return scoreToDecrease;
    }

    /**
     * à partir du point de l'écran touché, màj la valeur éventuellement touchée et donne un point
     * (0 si rien de touché)
     * @param touch
     * @return
     */
    public int getScoreFromTouchedPosition(Point touch) {
        Point found = null;
        for (Point p: numbersValues.keySet()) {
            if (distance(touch, p) <= RADIUS) {
                found = p;
            }
        }

        int ret = 0;

        if (found != null) {
            Integer curr = numbersValues.get(found);
            if (curr <= 1) {
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
        return Math.sqrt(Math.pow((x1-x2), 2) + Math.pow((y1-y2),2));
    }
}
