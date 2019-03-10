package helloandroid.m2dl.miniprojetmalylou;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, SensorEventListener {
    // components
    private SensorManager sm = null;
    private MeasureDisplay display;
    private MediaRecorder mRecorder;
    private Handler mHandler;

    // constants
    private static final int VAL_MAX = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        View view = findViewById(R.id.mainLayout);
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

        };*/

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
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        resizeLayout();
        initDisplayer();
    }

    private void resizeLayout() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);

        LinearLayout l = findViewById(R.id.layout);
        SurfaceView sv = findViewById(R.id.surfaceView);
        Button btn = findViewById(R.id.buttonStop);

        ViewGroup.LayoutParams params = l.getLayoutParams();
        params.height = size.y;
        params.width = size.x;

        sv.setMinimumHeight(l.getHeight()-60);
        sv.setMinimumWidth(l.getWidth()-2);

        btn.setWidth(l.getWidth()-2);
        btn.setHeight(60);
    }

    private void initDisplayer() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        display = new MeasureDisplay(size, (SurfaceView) findViewById(R.id.surfaceView));
    }

    private Runnable mSleepTask = new Runnable() {
        public void run() {
            mRecorder.start();
            //Noise monitoring start
            // Runnable(mPollTask) will execute after POLL_INTERVAL
            mHandler.postDelayed(eventSound, 1000);
        }
    };

    private Runnable eventSound = new Runnable() {
        public void run() {
            Double amp = 20 * Math.log10(mRecorder.getMaxAmplitude() / 2700.0);
            display.updatePtSonore(amp.intValue());
            // Runnable(mPollTask) will again execute after POLL_INTERVAL
            mHandler.postDelayed(eventSound, 1000);
        }
    };

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

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensor = event.sensor.getType();
        float [] values = event.values;

        synchronized (this) {
            if (sensor == Sensor.TYPE_MAGNETIC_FIELD) {
                float magField_x = values[0];
                float magField_y = values[1];
                float magField_z = values[2];
                double strength = Math.sqrt( (magField_x*magField_x) + (magField_y*magField_y) + (magField_z*magField_z) );
                int progress = (int) (strength) %VAL_MAX;

                display.updatePtAccelerometre(Math.abs(progress));
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
        int progress = (int) (posx*posy) %VAL_MAX;

        display.updatePtEcran(progress);
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

    public void makeUseOfNewLocation(Location location){
        int progress = (int) (location.getLatitude()*location.getLongitude()) %VAL_MAX;
        display.updatePtGPS(progress);
    }
}
