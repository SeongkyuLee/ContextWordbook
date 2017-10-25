package com.example.q.wordphoto;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Q on 9/23/17.
 */

public class WordPhotoParentActivity extends AppCompatActivity {
    @Override
    protected void onResume() {
        super.onResume();
        cancelNotification();
    }

    @Override
    protected void onPause() {
        super.onPause();
        startNotification();
    }

    protected void cancelNotification() {
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 10, new Intent(getApplicationContext(), WordAlarmReceiver.class), 0);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(alarmIntent);
        //Toast.makeText(this,"알림 종료", Toast.LENGTH_SHORT).show();
    }

    protected void startNotification() {
        new WordAlarmManager(getApplicationContext()).alarmElapsed();
        //Toast.makeText(this,"알림 시작", Toast.LENGTH_SHORT).show();
    }
}
