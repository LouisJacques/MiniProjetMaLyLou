package helloandroid.m2dl.miniprojetmalylou;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goGeoLocation(View view) {
        Intent intent = new Intent(this, Geolocation.class);
        startActivity(intent);
    }

    public void goLightSensor(View view) {
        Intent intent = new Intent(this, LightActivity.class);
        startActivity(intent);
    }

}
