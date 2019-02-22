package helloandroid.m2dl.miniprojetmalylou;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, SensorEventListener {
    TextView tv;
    private SensorManager sm = null;
    private Handler mHandler;
    private int cpt = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv = new TextView(this);
        tv.setText("Hi");

        //tv.setOnTouchListener(this);
        mHandler = new Handler();
        //mHandler.postDelayed(mUpdateTimeTask, 1000);
        setContentView(tv);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        setContentView(R.layout.activity_main);
        View view = (View) findViewById(R.id.mainLayout);
       // view.setText("pos x"  + "pos y" );
        view.setOnTouchListener(this);
    }
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {

            //if(cpt>1){
                //tv.setText(Build.MODEL + cpt+"");
                setContentView(tv);

                //cpt--;
                mHandler.postDelayed(this, 1000);

            //}else{
               // tv.setText("Hey");

                //System.exit(RESULT_OK);
                //finishActivity(RESULT_OK);

            //}
            sm = (SensorManager) getSystemService(SENSOR_SERVICE);

            //Handler mHandler = new Handler();
            //mHandler.postDelayed(this, 1000);
            //System.exit(RESULT_OK);
        }
    };
    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensor = event.sensor.getType();
        float [] values = event.values;

        synchronized (this) {
            if (sensor == Sensor.TYPE_MAGNETIC_FIELD) {
                float magField_x = values[0];
                float magField_y = values[1];
                float magField_z = values[2];
                TextView view = (TextView) findViewById(R.id.debugText);
                ProgressBar progressbar = (ProgressBar) findViewById(R.id.progressBarAccel);
                progressbar.setProgress((int) magField_z %100);
                view.setText("TYPE_MAGNETIC_FIELD x : " + magField_x +" y :"+magField_y+" z : "+magField_z);
               // setContentView(view);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float posx = event.getX();
        float posy = event.getY();
        cpt ++;
        //TextView view = (TextView) findViewById(R.id.debugText);
        //view.setText("pos x" + posx + "pos y" + posy);
        ProgressBar progressbar = (ProgressBar) findViewById(R.id.progressBarTactile);
        progressbar.setProgress((int) posx %100);
        //setContentView(progressbar);
        //setContentView(view);

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Sensor mMagneticField = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sm.registerListener(this, mMagneticField, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        sm.unregisterListener(this, sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
        super.onStop();
    }
}
