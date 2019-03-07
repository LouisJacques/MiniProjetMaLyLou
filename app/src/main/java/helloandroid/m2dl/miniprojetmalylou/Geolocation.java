package helloandroid.m2dl.miniprojetmalylou;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Geolocation extends AppCompatActivity {

    private LocationManager androidLocationManager;
    private Location location;
    private LocationListener androidLocationListener;
    private final static int REQUEST_CODE_UPDATE_LOCATION = 42;

    private Button buttontvAndroidLastLocation;
    private Button buttonGeotvAndroidGetLocation;

    private TextView tvAndroidLastLocation;
    private TextView tvAndroidGetLocation;
    private TextView tvAndroidUpdateLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geolocation);

        buttontvAndroidLastLocation = findViewById(R.id.btnAndroidGetLastLocation);
        buttonGeotvAndroidGetLocation = findViewById(R.id.btnAndroidGetLocation);

        tvAndroidLastLocation = findViewById(R.id.tvAndroidLastLocation);
        tvAndroidGetLocation = findViewById(R.id.tvAndroidGetLocation);
        tvAndroidUpdateLocation = findViewById(R.id.tvAndroidUpdateLocation);

        androidLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: faire quelque chose si besoin

            return;
        }

        location = androidLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            // Localisation disponible
            Toast.makeText(Geolocation.this, "Vous étiez récemment ici : " + location.getLatitude() + " / " + location.getLongitude(), Toast.LENGTH_LONG).show();
            tvAndroidLastLocation.setText(location.getLatitude() + " / " + location.getLongitude());
            System.out.println(location.getLatitude() + " / " + location.getLongitude());
        } else {
            // Pas de localisation disponible
        }

        androidLocationListener = new LocationListener() {
            public void onLocationChanged(Location loc) {
                Toast.makeText(
                        Geolocation.this,
                        "Vous êtes ici : " +
                                loc.getLatitude() +
                                " / " +
                                loc.getLongitude(),
                        Toast.LENGTH_LONG).show();
                tvAndroidGetLocation.setText(loc.getLatitude() + "/" + loc.getLongitude());
                System.out.println(loc.getLatitude() + " / " + loc.getLongitude());
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
                Toast.makeText(Geolocation.this, "Vous bougez, ... vous êtes ici : " + loc.getLatitude() + " / " + loc.getLongitude(), Toast.LENGTH_LONG).show();
                tvAndroidUpdateLocation.setText(loc.getLatitude() + " / " + loc.getLongitude());
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
                50, // en mètres
                androidLocationListener);
    }


    public void androidUpdateLocation(View view) {
        if (ActivityCompat.checkSelfPermission(Geolocation.this, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    Geolocation.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_UPDATE_LOCATION);
        } else {
            androidLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            androidLocationListener = new LocationListener() {
                public void onLocationChanged(Location loc) {
                    Toast.makeText(Geolocation.this, "Vous bougez, mois vous êtes ici : " + loc.getLatitude() + " / " + loc.getLongitude(), Toast.LENGTH_LONG).show();

                    tvAndroidUpdateLocation.setText(loc.getLatitude() + " / " + loc.getLongitude());
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_UPDATE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    androidUpdateLocation(null);
                } else {
                    Toast.makeText(Geolocation.this, "Permission refusée.", Toast.LENGTH_LONG).show();
                }
                return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (androidLocationListener != null) {
            if (androidLocationManager == null) {
                androidLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            }
            androidLocationManager.removeUpdates(androidLocationListener);
            androidLocationManager = null;
            androidLocationListener = null;
        }
    }

}
