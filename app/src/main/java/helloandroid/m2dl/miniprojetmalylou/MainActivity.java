package helloandroid.m2dl.miniprojetmalylou;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, SensorEventListener {
    TextView tv;
    private SensorManager sm = null;
    private Handler mHandler;
    private int cpt = 5;
    private MediaRecorder mRecorder;
    private Double amp;

    private LocationManager androidLocationManager;
    private Location location;
    private LocationListener androidLocationListener;
    private final static int REQUEST_CODE_UPDATE_LOCATION = 42;
    private final static int REQUEST_MIC_ACCESS = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv = new TextView(this);
        tv.setText("Hi");

        mHandler = new Handler();

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        setContentView(R.layout.activity_main);
        View view = (View) findViewById(R.id.mainLayout);

        androidLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        // androidLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.RECORD_AUDIO},
                    1);
            //location = androidLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else {
            //location = androidLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        //location = androidLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        /*
        if (location != null) {
            // Localisation disponible
            Toast.makeText(MainActivity.this, "Vous étiez récemment ici : " + location.getLatitude() + " / " + location.getLongitude(), Toast.LENGTH_LONG).show();
        } else {
            // Pas de localisation disponible
        }

        androidLocationListener = new LocationListener() {
            public void onLocationChanged(Location loc) {
                Toast.makeText(
                        MainActivity.this,
                        "Vous êtes ici : " +
                                loc.getLatitude() +
                                " / " +
                                loc.getLongitude(),
                        Toast.LENGTH_LONG).show();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };


        androidLocationManager.requestSingleUpdate(
                LocationManager.NETWORK_PROVIDER,
                androidLocationListener,
                null);


        androidLocationListener = new LocationListener() {
            public void onLocationChanged(Location loc) {
               /* Toast.makeText(Geolocation.this, "Vous bougez, ... vous êtes ici : " + loc.getLatitude() + " / " + loc.getLongitude(), Toast.LENGTH_LONG).show();
                tvAndroidUpdateLocation.setText(loc.getLatitude() + " / " + loc.getLongitude());
                System.out.println(loc.getLatitude() + " / " + loc.getLongitude());
                */
/*
                int gpsLatitude = (int) Math.abs(loc.getLatitude());
                System.out.println("gps latitude " + gpsLatitude);

                ProgressBar gpsProgressBar = (ProgressBar) findViewById(R.id.progressBarGPS);
                gpsProgressBar.setProgress(gpsLatitude);
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
                50, // en mètres
                androidLocationListener);
 */

        view.setOnTouchListener(this);
       /* if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_MIC_ACCESS);
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
        } */
    }

    public void androidUpdateLocation(View view) {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_UPDATE_LOCATION);
        } else {
            androidLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            androidLocationListener = new LocationListener() {
                public void onLocationChanged(Location loc) {
                    Toast.makeText(MainActivity.this, "Vous bougez, vous êtes ici : " + loc.getLatitude() + " / " + loc.getLongitude(), Toast.LENGTH_LONG).show();

                    int gpsLatitude = (int) Math.abs(loc.getLatitude());
                    System.out.println("gps latitude " + gpsLatitude);

                    ProgressBar gpsProgressBar = (ProgressBar) findViewById(R.id.progressBarGPS);
                    gpsProgressBar.setProgress(gpsLatitude);

                    System.out.println(loc.getLatitude() + " / " + loc.getLongitude());
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


    private Runnable mSleepTask = new Runnable() {
        public void run() {
            mRecorder.start();
            mHandler.postDelayed(eventSound, 1000);
        }
    };

    private Runnable eventSound = new Runnable() {
        public void run() {
            amp = 20 * Math.log10(mRecorder.getMaxAmplitude() / 2700.0);

            ProgressBar progressbar = (ProgressBar) findViewById(R.id.progressBar2);
            progressbar.setProgress(amp.intValue());

            mHandler.postDelayed(eventSound, 1000);
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

                System.out.println(magField_z);
                ProgressBar progressbar = (ProgressBar) findViewById(R.id.progressBarAccel);
                progressbar.setProgress((int) magField_z % 100);

            }

            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                int value = (int) event.values[0] / 100;
                ProgressBar lightProgressBar = (ProgressBar) findViewById(R.id.progressLight);
                lightProgressBar.setProgress(value);
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

        ProgressBar progressbar = (ProgressBar) findViewById(R.id.progressBarTactile);
        progressbar.setProgress((int) posx % 100);

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

}
