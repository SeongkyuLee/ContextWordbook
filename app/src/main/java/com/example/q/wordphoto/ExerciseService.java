package com.example.q.wordphoto;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.NotificationCompat;

public class ExerciseService extends Service {
    NotificationManager mNotifyManger;
    ServiceThread thread;
    Notification mNotify;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mNotifyManger = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread(handler);
        thread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        thread.stopForever();
        thread = null;
    }

    class myServiceHandler extends Handler {
        public void handleMessage(Message msg) {
            Intent intent = new Intent(ExerciseService.this, ExerciseServiceActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(ExerciseService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            mNotify = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle("Title Exercise")
                    .setContentText("Text Exercise")
                    .setSmallIcon(R.drawable.card)
                    .setTicker("알림!!")
                    .setContentIntent(pendingIntent)
                    .build();

            //mNotify.defaults = Notification.DEFAULT_SOUND;
            //mNotify.flags = Notification.FLAG_ONLY_ALERT_ONCE;
            //mNotify.flags = Notification.FLAG_AUTO_CANCEL;

            mNotifyManger.notify(777, mNotify);
        }
    }

    class ServiceThread extends Thread{
        android.os.Handler handler;
        boolean isRun = true;

        public ServiceThread(android.os.Handler handler){
            this.handler = handler;
        }

        public void stopForever(){
            synchronized (this) {
                this.isRun = false;
            }
        }

        public void run(){
            //반복적으로 수행할 작업을 한다.
            while(isRun){
                handler.sendEmptyMessage(0);//쓰레드에 있는 핸들러에게 메세지를 보냄
                try{
                    Thread.sleep(10000); //10초씩 쉰다.
                }catch (Exception e) {}
            }
        }
    }
}
