package helloandroid.m2dl.miniprojetmalylou;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AnimationBallActivity extends AppCompatActivity {
    private static final String TAG = "Animation Ball";

    ImageView img;
    ConstraintLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_ball);

        Button bounceBallButton = (Button) findViewById(R.id.bounceBallButton);
        final ImageView bounceBallImage = (ImageView) findViewById(R.id.bounceBallImage);

         root = (ConstraintLayout) findViewById( R.id.rootLayout);

        ((Button) findViewById( R.id.btn1 )).setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                img = (ImageView) findViewById( R.id.img1 );
                System.out.println(img.getX() + " " + img.getY() + root.getMaxHeight() + " " + root.getMaxWidth());
                moveViewToScreenCenter( img );
                img = (ImageView) findViewById( R.id.img2 );
                moveViewToScreenCenter( img );
                img = (ImageView) findViewById( R.id.img3 );
                moveViewToScreenCenter( img );
                img = (ImageView) findViewById( R.id.img4 );
                moveViewToScreenCenter( img );

                for(int i=0; i<10; i++) {
                    double height = Math.random() * ((double) root.getMaxHeight() - (double) root.getMinHeight());
                    double width = Math.random() * ((double) root.getMaxWidth() - (double) root.getMinWidth());

                    img.setX((float) height);
                    img.setY((float) width);
                }
            }
        });

        bounceBallButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                bounceBallImage.clearAnimation();
                TranslateAnimation transAnim = new TranslateAnimation(0, 0, 0,
                        getDisplayHeight()/2);
                //transAnim.setStartOffset(500);
                transAnim.setDuration(3000);
                transAnim.setFillAfter(true);
               // transAnim.setInterpolator(new BounceInterpolator());
                transAnim.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                        Log.i(TAG, "Lancement");

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // TODO Auto-generated method
                        Log.i(TAG, "Repeat");

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Log.i(TAG, "Fin de l'animation");
                        bounceBallImage.clearAnimation();
                        final int left = bounceBallImage.getLeft();
                        final int top = bounceBallImage.getTop();
                        final int right = bounceBallImage.getRight();
                        final int bottom = bounceBallImage.getBottom();
                        bounceBallImage.layout(left, top, right, bottom);

                        root.removeView(bounceBallImage);

                    }
                });
                bounceBallImage.startAnimation(transAnim);
            }
        });

    }

    private int getDisplayHeight() {
        return this.getResources().getDisplayMetrics().heightPixels;
    }

    private void moveViewToScreenCenter( View view )
    {
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics( dm );
        int statusBarOffset = dm.heightPixels - root.getMeasuredHeight();

        int originalPos[] = new int[2];
        view.getLocationOnScreen( originalPos );

        int xDest = dm.widthPixels/2;
        int yDest = (view.getMeasuredWidth()/2);

       // xDest  = dm.heightPixels/2 - (view.getMeasuredHeight()/2) - statusBarOffset;

        TranslateAnimation anim = new TranslateAnimation( 0, 0 , 0, yDest + originalPos[1] + 900);
        anim.setDuration(1000);
        anim.setFillAfter( true );
        view.startAnimation(anim);

    }
}
