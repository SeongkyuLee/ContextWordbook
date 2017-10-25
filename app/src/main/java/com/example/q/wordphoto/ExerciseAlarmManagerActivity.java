package com.example.q.wordphoto;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ExerciseAlarmManagerActivity extends WordPhotoParentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_manager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*
        Intent intent = new Intent(this, ExerciseWordAlarmService.class);
        startService(intent);
        */
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 10, new Intent(getApplicationContext(), WordAlarmReceiver.class), 0);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(alarmIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        new WordAlarmManager(getApplicationContext()).alarmElapsed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

