package com.example.q.wordphoto;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * Created by Q on 9/21/17.
 */

public class WordAlarmManager {
    private Context mContext;
    private AlarmManager mAlarmManager;

    public WordAlarmManager(Context mContext) {
        this.mContext = mContext;
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }
    public void alarmRTC() {
        Intent intent = new Intent(mContext, WordAlarmReceiver.class);

        PendingIntent sender = PendingIntent.getBroadcast(mContext, 0, intent, 0);

        java.util.Calendar calendar = java.util.Calendar.getInstance();

        calendar.set(calendar.get(java.util.Calendar.YEAR)
                , calendar.get(java.util.Calendar.MONTH)
                , calendar.get(java.util.Calendar.DATE)
                , calendar.get(java.util.Calendar.HOUR)
                , calendar.get(java.util.Calendar.MINUTE)
                , calendar.get(java.util.Calendar.SECOND) + 5);

        // 정확한 시간에 한 번만 알려줌
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
    }

    public void alarmElapsed() {
        PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, 10, new Intent(mContext, WordAlarmReceiver.class), 0);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(calendar.get(java.util.Calendar.YEAR)
                , calendar.get(java.util.Calendar.MONTH)
                , calendar.get(java.util.Calendar.DATE)
                , calendar.get(java.util.Calendar.HOUR)
                , calendar.get(java.util.Calendar.MINUTE)
                , calendar.get(java.util.Calendar.SECOND) + 1000);

        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME
                ,SystemClock.elapsedRealtime()
                ,10 * 1000
                ,alarmIntent);
    }
}

