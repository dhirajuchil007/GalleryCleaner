package com.velocityappsdj.gallerycleaner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Calendar;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private AdView mAdView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-9540086841520699~9425161569");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("4E0FA1397589AB27")
                .addTestDevice("36B6BD5B0401A0FD")
                .build();
        mAdView.loadAd(adRequest);
        Button startButton=(Button)findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(StartActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        ImageView alertButton=(ImageView) findViewById(R.id.alert_button);
        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(StartActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        calendar.set(Calendar.MINUTE, selectedMinute);
                        calendar.set(Calendar.SECOND, 0);
                        Intent intent1 = new Intent(StartActivity.this, AlarmReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(StartActivity.this, 0,intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager am = (AlarmManager) StartActivity.this.getSystemService(StartActivity.this.ALARM_SERVICE);
                        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        ImageView settingButton=(ImageView)findViewById(R.id.setting_button);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(StartActivity.this,SettingsActivity.class);
                startActivity(intent);
            }
        });
    }








}
