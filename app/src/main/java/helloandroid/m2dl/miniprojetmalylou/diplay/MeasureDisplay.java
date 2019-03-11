package helloandroid.m2dl.miniprojetmalylou.diplay;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.view.SurfaceView;

import java.util.ArrayList;

public class MeasureDisplay {
    private SurfaceView sv;

    // points
    /*
            son
    lumière     acceletomètre
    gps         ecran
    */
    private Point ptSonore, ptGPS, ptLuminosite, ptAccelerometre, ptEcranTactile;
    private Point ptCentre;

    private ArrayList<Path> hexagonePaths = new ArrayList<>();

    // constants
    private static final int VAL_MAX = 200;
    private static final int TICKS_PER_SECOND = 25;
    private static final int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
    private static final int MAX_FRAMESKIP = 5;

    private Handler handler;

    private Point size;

    public MeasureDisplay(Point size, SurfaceView sv) {
        this.size = size;
        this.sv = sv;
        setPointsAndLists();
    }

    /*public void startDrawing() {
        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    long next_tick = System.currentTimeMillis();
                    int loops = 0;
                    while (System.currentTimeMillis() > next_tick
                            && loops < MAX_FRAMESKIP) {


                        next_tick += SKIP_TICKS;
                        loops++;
                    }
                    draw();
                }
            }
        });
    }*/

    // METHODES SUR LES ACTIONS SUR LES POINTS DES MESURES

    /**
     * initialise les 5 points de mesure + leurs milieux + le ptCentre
     * initialise aussi les 5 hexagones de niveaux (20%->100%)
     */
    private void setPointsAndLists() {
        // points variables
        initMeasurePoints();

        // hexagones (points fixes)
        hexagonePaths.add(getPath(getHexagone(1)));
        hexagonePaths.add(getPath(getHexagone(2)));
        hexagonePaths.add(getPath(getHexagone(3)));
        hexagonePaths.add(getPath(getHexagone(4)));
        hexagonePaths.add(getPath(getHexagone(5)));
    }

    /**
     * Positionne initialement tous les points au ptCentre de l'appareil
     */
    private void initMeasurePoints() {
        int x2 = size.x/2, y2 = size.y/2;

        ptCentre = new Point(x2, y2);
        ptSonore = new Point(x2, y2);
        ptAccelerometre = new Point(x2, y2);
        ptEcranTactile = new Point(x2, y2);
        ptLuminosite = new Point(x2, y2);
        ptGPS = new Point(x2, y2);
    }

    /**
     * Dessiner
     *
     */
    private void draw() {
        Canvas canvas = sv.getHolder().lockCanvas();

        if (canvas == null) {
            return;
        }
        // paint
        Paint polyPaint = new Paint();
        polyPaint.setStrokeWidth(2);
        polyPaint.setStrokeCap(Paint.Cap.ROUND);

        // path
        Path polyPath = getPath(getMeasurePointList());

        // draw
        // clear
        polyPaint.setColor(Color.BLACK);
        polyPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0,0,canvas.getWidth(),canvas.getHeight(),polyPaint);
        // hexagone
        polyPaint.setColor(Color.WHITE);
        polyPaint.setStyle(Paint.Style.STROKE);
        for (Path pathHexa: hexagonePaths) {
            canvas.drawPath(pathHexa, polyPaint);
        }
        // points
        polyPaint.setColor(Color.RED);
        canvas.drawCircle((float) ptCentre.x, (float) ptCentre.y, 2, polyPaint);
        polyPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(polyPath, polyPaint);

        drawText(canvas, polyPaint);

        sv.getHolder().unlockCanvasAndPost(canvas);
    }

    private void drawText(Canvas canvas, Paint polyPaint) {
        polyPaint.setStyle(Paint.Style.STROKE);
        polyPaint.setColor(Color.WHITE);
        polyPaint.setTextSize(VAL_MAX/7);
        ArrayList<Point> hexa = getHexagone(5);

        canvas.drawText("Son",hexa.get(0).x - VAL_MAX/10, hexa.get(0).y - VAL_MAX/5, polyPaint);
        canvas.drawText("Accéléromètre",hexa.get(1).x - 3*VAL_MAX/4, hexa.get(1).y - VAL_MAX/2, polyPaint);
        canvas.drawText("Ecran tactile",hexa.get(2).x + VAL_MAX/10, hexa.get(2).y, polyPaint);
        canvas.drawText("GPS",hexa.get(3).x - 2*VAL_MAX/5, hexa.get(3).y, polyPaint);
        canvas.drawText("Lumière",hexa.get(4).x, hexa.get(4).y - VAL_MAX/2, polyPaint);
    }

    /**
     * ralentit le process pour l'affichage
     * @param next_tick
     */
    private void waitAndDraw(double next_tick) {
        int loops = 0;
        while (System.currentTimeMillis() > next_tick
                && loops < MAX_FRAMESKIP/2) {


            next_tick += SKIP_TICKS;
            loops++;
        }
        //draw(); // à décommenter si besoin
    }

    /**
     * Points de l'hexagone de contour
     * @param level 1 à 5
     * @return
     */
    private ArrayList<Point> getHexagone(int level) {
        int valmax_level =VAL_MAX * level;
        int valmax_3_10 = valmax_level * 3 / 10;
        int valmax_3_20 = valmax_level * 3 / 20;
        int valmax_1_20 = valmax_level * 1 / 20;

        ArrayList<Point> listHexa = new ArrayList<>();
        listHexa.add(new Point(ptCentre.x, ptCentre.y-valmax_3_10));
        listHexa.add(new Point(ptCentre.x+valmax_3_10, ptCentre.y-valmax_1_20));
        listHexa.add(new Point(ptCentre.x+valmax_3_20, ptCentre.y+valmax_3_10));
        listHexa.add(new Point(ptCentre.x-valmax_3_20, ptCentre.y+valmax_3_10));
        listHexa.add(new Point(ptCentre.x-valmax_3_10, ptCentre.y-valmax_1_20));

        return listHexa;
    }

    /**
     * points de mesure de données
     * @return
     */
    private ArrayList<Point> getMeasurePointList() {
        ArrayList<Point> list = new ArrayList<>();
        Point last = null;
        if (!ptSonore.equals(ptCentre)) {
            list.add(ptSonore);
            last = ptSonore;
        }
        if (!ptAccelerometre.equals(ptCentre)) {
            if (last != null) {
                list.add(getBarycentre(last,ptAccelerometre));
            }
            list.add(ptAccelerometre);
            last = ptAccelerometre;
        }
        if (!ptEcranTactile.equals(ptCentre)) {
            if (last != null) {
                list.add(getBarycentre(last,ptEcranTactile));
            }
            list.add(ptEcranTactile);
            last = ptEcranTactile;
        }
        if (!ptGPS.equals(ptCentre)) {
            if (last != null) {
                list.add(getBarycentre(last,ptGPS));
            }
            list.add(ptGPS);
            last = ptGPS;
        }
        if (!ptLuminosite.equals(ptCentre)) {
            if (last != null) {
                list.add(getBarycentre(last,ptLuminosite));
            }
            list.add(ptLuminosite);
            last = ptLuminosite;
        }

        if (list.size() == 1) {
            list.add(ptCentre);
        } else if (list.size() > 1) {
            list.add(getBarycentre(last, list.get(0)));
        }

        return list;
    }

    /**
     * Convertit la liste en Path
     * @param list
     * @return polyPath
     */
    private Path getPath(ArrayList<Point> list) {
        Path polyPath = new Path();
        polyPath.moveTo(list.get(0).x, list.get(0).y);
        for (Point p: list) {
            polyPath.lineTo(p.x, p.y);
        }
        polyPath.lineTo(list.get(0).x, list.get(0).y);

        return polyPath;
    }

    /**
     * Barycentre de a,b, et ptCentre
     * @param a
     * @param b
     * @return
     */
    private Point getBarycentre(Point a, Point b) {
        return new Point((a.x+b.x+ ptCentre.x)/3,(a.y+b.y+ ptCentre.y)/3);
    }

    // Point update

    public void updatePtEcran(int progress) {
        double next_tick = System.currentTimeMillis();
        Point goal = new Point(ptCentre.x + progress*3/4, ptCentre.y + progress*3/2);
        moveAndDraw(goal, ptEcranTactile, next_tick);
    }

    public void updatePtGPS(int progress) {
        double next_tick = System.currentTimeMillis();
        Point goal = new Point(ptCentre.x - progress*3/4, ptCentre.y + progress*3/4);
        moveAndDraw(goal, ptGPS, next_tick);
    }

    public void updatePtSonore(int progress) {
        double next_tick = System.currentTimeMillis();
        Point goal = new Point(ptCentre.x, ptCentre.y - progress*3/4);
        moveAndDraw(goal, ptSonore, next_tick);
    }

    public void updatePtLum(int progress) {
        double next_tick = System.currentTimeMillis();
        Point goal = new Point(ptCentre.x - progress*3/2, ptCentre.y - progress*1/4);
        moveAndDraw(goal, ptLuminosite, next_tick);
    }

    public void updatePtAccelerometre(int progress) {
        double next_tick = System.currentTimeMillis();
        Point goal = new Point(ptCentre.x + progress*3/2, ptCentre.y - progress*1/4);
        moveAndDraw(goal, ptAccelerometre, next_tick);
    }

    /**
     * déplace un point vers le point goal
     * @param goal
     * @param toChange
     * @param next_tick
     */
    private void moveAndDraw(Point goal, Point toChange, double next_tick) {
        int x=toChange.x, y=toChange.y;
        while (x!=goal.x || y!=goal.y) {
            toChange.set(x, y);
            if (x < goal.x) {
                x++;
            } else if (x > goal.x) {
                x--;
            }
            if (y < goal.y) {
                y++;
            } else if (y > goal.y) {
                y--;
            }

            waitAndDraw(next_tick);
        }
        toChange.set(x, y);
        draw();
    }
}
