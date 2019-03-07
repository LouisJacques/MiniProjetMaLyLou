package helloandroid.m2dl.miniprojetmalylou;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, SensorEventListener {
    TextView tv;
    private SensorManager sm = null;
    private Handler mHandler;
    private int cpt = 5;
    private MediaRecorder mRecorder;
    private Double amp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv = new TextView(this);
        tv.setText("Hi");

        //tv.setOnTouchListener(this);
        mHandler = new Handler();
        //mHandler.postDelayed(mUpdateTimeTask, 1000);
        // setContentView(tv);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        setContentView(R.layout.activity_main);
        View view = (View) findViewById(R.id.mainLayout);
        // view.setText("pos x"  + "pos y" );
        /*LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }

        }; */

        view.setOnTouchListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    10);
            /*mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try {
                mRecorder.prepare();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mRecorder.start(); */
        } else {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try {
                mRecorder.prepare();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSleepTask.run();
        }
        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);

            return;
        } */
        /*locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        System.out.println("AAAAAAAAA" +locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)); */


    }

    private Runnable mSleepTask = new Runnable() {
        public void run() {
            //Log.i("Noise", "runnable mSleepTask");
            mRecorder.start();
            /*if (!mWakeLock.isHeld()) {
                mWakeLock.acquire();
            } */
            //Noise monitoring start
            // Runnable(mPollTask) will execute after POLL_INTERVAL
            mHandler.postDelayed(eventSound, 1000);
        }
    };
    private Runnable eventSound = new Runnable() {
        public void run() {
            amp = 20 * Math.log10(mRecorder.getMaxAmplitude() / 2700.0);

            //TextView view = (TextView) findViewById(R.id.debugText);
            //view.setText("Sound : " + amp + " db");
            ProgressBar progressbar = (ProgressBar) findViewById(R.id.progressBar2);
            progressbar.setProgress(amp.intValue() );
            //Log.i("Noise", "runnable mPollTask");
            // Runnable(mPollTask) will again execute after POLL_INTERVAL
            mHandler.postDelayed(eventSound, 1000);
        }
    };

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
        float[] values = event.values;

        synchronized (this) {
            if (sensor == Sensor.TYPE_MAGNETIC_FIELD) {
                float magField_x = values[0];
                float magField_y = values[1];
                float magField_z = values[2];
                //TextView view = (TextView) findViewById(R.id.debugText);
                ProgressBar progressbar = (ProgressBar) findViewById(R.id.progressBarAccel);
                progressbar.setProgress((int) magField_z % 100);
                //view.setText("TYPE_MAGNETIC_FIELD x : " + magField_x +" y :"+magField_y+" z : "+magField_z);
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
        cpt++;
        //TextView view = (TextView) findViewById(R.id.debugText);
        //view.setText("pos x" + posx + "pos y" + posy);
        ProgressBar progressbar = (ProgressBar) findViewById(R.id.progressBarTactile);
        progressbar.setProgress((int) posx % 100);
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

    public void makeUseOfNewLocation(Location location) {
        ProgressBar progressbar = (ProgressBar) findViewById(R.id.progressBarGPS);
        progressbar.setProgress((int) location.getLatitude() % 100);
        TextView view = (TextView) findViewById(R.id.debugText);
        view.setText("Latitude : " + location.getLatitude() + " Longitude :" + location.getLongitude() + " Altitude : " + location.getAltitude());
        setContentView(view);

        System.out.println("loc has changed");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setOutputFile("/dev/null");
                try {
                    mRecorder.prepare();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //mRecorder.start();
                mSleepTask.run();
            } else {
                //User denied Permission.
            }
        }
    }

}
