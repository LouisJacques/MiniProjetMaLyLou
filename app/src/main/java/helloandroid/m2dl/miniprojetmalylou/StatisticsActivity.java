package helloandroid.m2dl.miniprojetmalylou;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {
    ArrayList<Integer> soundList = new ArrayList<Integer>();
    ArrayList<Integer> gpsList = new ArrayList<Integer>();
    ArrayList<Integer> lightList = new ArrayList<Integer>();
    ArrayList<Integer> accList = new ArrayList<Integer>();
    ArrayList<Integer> touchList = new ArrayList<Integer>();
    CheckBox accCheckBox;
    CheckBox soundCheckBox;
    CheckBox gpsCheckBox;
    CheckBox lightCheckBox;
    CheckBox touchCheckBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        if(getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            String res = "";
            if (bundle.getIntegerArrayList("valuesLight") != null) {
                lightList.addAll(bundle.getIntegerArrayList("valuesLight"));
                TextView text = (TextView)findViewById(R.id.lightTextView);
                text.setText(formatList(lightList));
            }
            if (bundle.getIntegerArrayList("valuesGPS") != null) {
                gpsList.addAll(bundle.getIntegerArrayList("valuesGPS"));
                TextView text = (TextView)findViewById(R.id.gpsTextView);
                text.setText(formatList(gpsList));
            }
            if (bundle.getIntegerArrayList("valuesTouch") != null) {
                touchList.addAll(bundle.getIntegerArrayList("valuesTouch"));
                TextView text = (TextView)findViewById(R.id.touchTextView);
                text.setText(formatList(touchList));
            }
            if (bundle.getIntegerArrayList("valuesAcc") != null) {
                accList.addAll(bundle.getIntegerArrayList("valuesAcc"));
                TextView text = (TextView)findViewById(R.id.accTextView);
                text.setText(formatList(accList));
            }
            if (bundle.getIntegerArrayList("valuesSound") != null) {
                soundList.addAll(bundle.getIntegerArrayList("valuesSound"));
                TextView text = (TextView)findViewById(R.id.soundTextView);
                text.setText(formatList(soundList));
            }
        }
    }

    public String formatList(ArrayList<Integer> list){
        String res = "";
        for (Integer i : list){
            res += "["+i+"] ";
        }
        return res;
    }
    public void launchGame(View view){
        Intent activityGame = new Intent(getApplicationContext(), GameActivity.class);
        accCheckBox = (CheckBox) findViewById(R.id.accCheckBox);
        soundCheckBox = (CheckBox) findViewById(R.id.soundCheckBox);
        gpsCheckBox = (CheckBox) findViewById(R.id.gpsCheckBox);
        touchCheckBox = (CheckBox) findViewById(R.id.touchcheckBox);
        lightCheckBox = (CheckBox) findViewById(R.id.lightCheckBox);

        //activityGame.putExtra("key", "VALEUR TRANSFEREE");
        if(accCheckBox.isChecked()){
            activityGame.putExtra("valuesAcc", accList);

        }
        if(soundCheckBox.isChecked()){
            activityGame.putExtra("valuesSound", soundList);

        }
        if(gpsCheckBox.isChecked()){
            activityGame.putExtra("valuesGPS", gpsList);

        }
        if(touchCheckBox.isChecked()){
            activityGame.putExtra("valuesTouch", touchList);

        }
        if(lightCheckBox.isChecked()){
            activityGame.putExtra("valuesLight", lightList);

        }


        startActivity(activityGame);
            }
}
