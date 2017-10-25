package com.example.q.wordphoto;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

public class ExerciseWordAlarmService extends Service {
    PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 10, new Intent(getApplicationContext(), WordAlarmReceiver.class), 0);
    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

    Handler wordAlarmHandler;
    Thread wordAlarmThread = new Thread() {
        boolean isRun = true;
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ComponentName currentActivityName;

        @Override
        public void run() {
            // 해당 앱이 실행되는 동안에는 알림을 주지 않는다
            alarmManager.cancel(alarmIntent);

            while(isRun) {
                try {
                    Thread.sleep(2 * 1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                currentActivityName = activityManager.getRunningTasks(1).get(0).topActivity;

                if(!currentActivityName.toString().equals("com.example.q.wordphoto")) {
                    wordAlarmHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "실행 중", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    wordAlarmHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "종료", Toast.LENGTH_SHORT).show();
                        }
                    });
                    new WordAlarmManager(getApplicationContext()).alarmElapsed();
                    isRun = false;
                }
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        wordAlarmThread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
