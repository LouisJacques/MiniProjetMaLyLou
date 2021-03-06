package helloandroid.m2dl.miniprojetmalylou;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import helloandroid.m2dl.miniprojetmalylou.diplay.MeasureDisplay;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, SensorEventListener {
    // components
    private SensorManager sm = null;
    private MeasureDisplay display;
    private MediaRecorder mRecorder;
    private Handler mHandler;

    private LocationManager androidLocationManager;
    private Location location;
    private LocationListener androidLocationListener;
    private final static int REQUEST_CODE_UPDATE_LOCATION = 42;
    private final static int REQUEST_MIC_ACCESS = 10;

    ArrayList<Integer> soundList = new ArrayList<Integer>();
    ArrayList<Integer> gpsList = new ArrayList<Integer>();
    ArrayList<Integer> lightList = new ArrayList<Integer>();
    ArrayList<Integer> accList = new ArrayList<Integer>();
    ArrayList<Integer> touchList = new ArrayList<Integer>();

    int soundcpt = 0;
    int gpscpt= 0;
    int lightcpt= 0;
    int acccpt= 0;
    int touchcpt= 0;
    // constants
    private int VAL_MAX = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resizeLayout();
        initDisplayer();

        mHandler = new Handler();
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);

        View view = findViewById(R.id.mainLayout);

        view.setOnTouchListener(this);

        // androidLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.RECORD_AUDIO},
                    1);
            //location = androidLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else {
            //location = androidLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            androidUpdateLocation(null);

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
    }

    public void androidUpdateLocation(View view) {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_UPDATE_LOCATION);
        } else {
            androidLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            androidLocationListener = new LocationListener() {
                public void onLocationChanged(Location loc) {
                    int progress = (int) Math.abs(loc.getLatitude()*loc.getLongitude()) % VAL_MAX;
                    if (progress == 0) {
                        progress = 1;
                    }
                    display.updatePtGPS(progress);
                    gpscpt= save(gpsList,gpscpt,(Math.abs(progress)));

                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };

            androidLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000, // en millisecondes
                    10, // en mètres
                    androidLocationListener);
        }
    }

    private void resizeLayout() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);

        VAL_MAX = 5 * size.x / 18;

        LinearLayout l = findViewById(R.id.layout);
        SurfaceView sv = findViewById(R.id.surfaceView);
        Button btn = findViewById(R.id.buttonStop);

        ViewGroup.LayoutParams params = l.getLayoutParams();
        params.height = size.y;
        params.width = size.x;

        ViewGroup.LayoutParams paramsSv = sv.getLayoutParams();
        paramsSv.height = size.y - 60;
        paramsSv.width = size.x;
        btn.setWidth(size.x);
        btn.setHeight(60);
    }

    private void initDisplayer() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        display = new MeasureDisplay(size, (SurfaceView) findViewById(R.id.surfaceView));
    }

    private Runnable eventSound = new Runnable() {
        public void run() {
            Double amp = 190 * Math.log10(mRecorder.getMaxAmplitude() / 2700.0);

            int calcul = (Math.abs(amp.intValue()*2)%VAL_MAX);
            if (calcul == 0) {
                calcul = 1;
            }
            display.updatePtSonore(calcul);

            soundcpt = save(soundList,soundcpt,calcul);

            mHandler.postDelayed(eventSound, 1000);
        }
    };

    private Runnable mSleepTask = new Runnable() {
        public void run() {
            mRecorder.start();
            mHandler.postDelayed(eventSound, 1000);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_UPDATE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    androidUpdateLocation(null);
                } else {
                    Toast.makeText(MainActivity.this, "Permission refusée.", Toast.LENGTH_LONG).show();
                }
                break;

            case REQUEST_MIC_ACCESS:
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
                    mSleepTask.run();
                } else {
                    //User denied Permission.
                }
                break;
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    androidUpdateLocation(null);
                } else {
                    Toast.makeText(MainActivity.this, "Permission refusée.", Toast.LENGTH_LONG).show();
                }
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
                int value =  Math.abs((int)(Math.abs(values[0]) +  Math.abs(values[1]) +  Math.abs(values[2])));

                Sensor magneticSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                int maxvalue = (int)magneticSensor.getMaximumRange();
                int progress  = (VAL_MAX * value )/maxvalue;

                if (progress == 0) {
                    progress = 1;
                }
                display.updatePtAccelerometre(Math.abs(value));
                acccpt = save(accList,acccpt,(Math.abs(progress)));
            } else if (sensor == Sensor.TYPE_LIGHT) {
                int value = (int) (event.values[0]);

                Sensor light = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
                int maxvalue = (int)light.getMaximumRange();
                int progress = (value * VAL_MAX ) / maxvalue;

                if (progress == 0) {
                    progress = 1;
                }
                display.updatePtLum(progress);
                lightcpt = save(lightList,lightcpt,(Math.abs(progress)));
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

        if (progress == 0) {
            progress = 1;
        }
        display.updatePtEcran(progress);
        touchcpt = save(touchList,touchcpt,(Math.abs(progress)));
        return false;
    }



    @Override
    protected void onResume() {
        super.onResume();
        Sensor mMagneticField = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sm.registerListener(this, mMagneticField, SensorManager.SENSOR_DELAY_NORMAL);

        sm.registerListener(
                this,
                sm.getDefaultSensor(Sensor.TYPE_LIGHT),
                SensorManager.SENSOR_DELAY_NORMAL
        );
    }

    @Override
    protected void onStop() {
        sm.unregisterListener(this, sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));

        sm.unregisterListener(this, sm.getDefaultSensor(Sensor.TYPE_LIGHT));

        if (androidLocationListener != null) {
            if (androidLocationManager == null) {
                androidLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            }
            androidLocationManager.removeUpdates(androidLocationListener);
            androidLocationManager = null;
            androidLocationListener = null;
        }

        super.onStop();
    }

    public void makeUseOfNewLocation(Location location){
        int progress = (int) (location.getLatitude()*location.getLongitude()) %VAL_MAX;

        if (progress == 0) {
            progress = 1;
        }
        display.updatePtGPS(progress);
    }
    public int save(ArrayList<Integer> list, int cpt, int val){
        if(cpt<5){
            list.add(val);
            cpt++;
        }else{
            list.set(cpt-1,val);
            cpt = (cpt+1)%4;
        }
        return cpt;
    }

    public void launchGame(View view){
        Intent activityGame = new Intent(getApplicationContext(), GameActivity.class);
        Intent activitySettings = new Intent(getApplicationContext(), StatisticsActivity.class);
        sm.unregisterListener(this, sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));

        sm.unregisterListener(this, sm.getDefaultSensor(Sensor.TYPE_LIGHT));

        if (androidLocationListener != null) {
            if (androidLocationManager == null) {
                androidLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            }
            androidLocationManager.removeUpdates(androidLocationListener);
            androidLocationManager = null;
            androidLocationListener = null;
        }
        //activityGame.putExtra("key", "VALEUR TRANSFEREE");
        activityGame.putExtra("valuesLight", lightList);
        activityGame.putExtra("valuesGPS", gpsList);
        activityGame.putExtra("valuesTouch", touchList);
        activityGame.putExtra("valuesAcc", accList);
        activityGame.putExtra("valuesSound", soundList);

        activitySettings.putExtra("valuesLight", lightList);
        activitySettings.putExtra("valuesGPS", gpsList);
        activitySettings.putExtra("valuesTouch", touchList);
        activitySettings.putExtra("valuesAcc", accList);
        activitySettings.putExtra("valuesSound", soundList);
        //startActivity(activityGame);

        startActivity(activitySettings);
    }

    public void goAnimationBall(View view) {
        Intent intentAnimationBall = new Intent(getApplicationContext(), AnimationBallActivity.class);
        startActivity(intentAnimationBall);
    }
}
