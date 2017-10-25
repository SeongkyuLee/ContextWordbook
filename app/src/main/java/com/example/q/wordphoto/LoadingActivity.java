package com.example.q.wordphoto;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

public class LoadingActivity extends WordPhotoParentActivity {
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        mProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);
        startLoading();

    }

    private void startLoading() {
        AsyncTask loadingTask = new AsyncTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressBar.setVisibility(View.VISIBLE);
                Animation loadingPhotoAnimation = AnimationUtils.loadAnimation(LoadingActivity.this, R.anim.loading_photo_anim);
                mProgressBar.startAnimation(loadingPhotoAnimation);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                //Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                startActivity(intent);
                mProgressBar.setVisibility(View.INVISIBLE);
                finish();
            }

            @Override
            protected void onProgressUpdate(Object[] values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        loadingTask.execute();

        /*
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
        */
    }
}
