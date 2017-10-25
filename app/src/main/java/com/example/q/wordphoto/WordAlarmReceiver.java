package com.example.q.wordphoto;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class WordAlarmReceiver extends BroadcastReceiver {
    Notification mNotification;
    Context mContext;

    static final String WORDS_PREFS_NAME = "WordItems";
    SharedPreferences wordPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        wordPreferences = context.getSharedPreferences(WORDS_PREFS_NAME, Context.MODE_PRIVATE);
        int length = wordPreferences.getInt("length",0);

        if(length > 0) {
            int index = wordPreferences.getInt("alarmIndex",0);
            String id = wordPreferences.getString(Integer.toString(index), null);
            String word = wordPreferences.getString(id+"_word", null);
            String meaning = wordPreferences.getString(id+"_meaning", null);
            String photoPath = wordPreferences.getString(id+"_photoPath", null);

            mContext = context;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Builder builder = new Notification.Builder(context);
            builder.setSmallIcon(R.drawable.card)
                    .setTicker("단어 암기")
                    .setWhen(System.currentTimeMillis())
                    .setNumber(1)
                    .setContentTitle(word)
                    .setContentText(meaning)
                    .setLargeIcon(getBitmap(photoPath))
                    .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            mNotification = builder.build();
            notificationManager.notify(1, mNotification);

            SharedPreferences.Editor editor = wordPreferences.edit();
            index++;
            if(index >= length) {
                index = 0;
            }
            editor.putInt("alarmIndex",index);
            editor.apply();
        }
    }

    private Bitmap getBitmap(String photoPath) {
        Bitmap tempPhoto = null;

        if(photoPath == null)
            return null;

        try {
            Uri imageUri = Uri.parse(photoPath);
            InputStream imageStream = null;
            imageStream = mContext.getContentResolver().openInputStream(imageUri);
            tempPhoto = BitmapFactory.decodeStream(imageStream);
            tempPhoto = Bitmap.createScaledBitmap(tempPhoto, 30, 30, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tempPhoto;
    }
}
