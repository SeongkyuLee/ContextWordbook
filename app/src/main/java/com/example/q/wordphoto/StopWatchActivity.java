package com.example.q.wordphoto;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class StopWatchActivity extends WordPhotoParentActivity {
    static final String TIME_PREFS_NAME = "Time";
    SharedPreferences timesPrefs;

    final static int IDLE = 0;
    final static int RUNNING = 1;
    final static int PAUSE = 2;

    Handler mTimerHandler;

    TextView mTimeTextView;
    TextView mLapTimeTextView;
    ListView mStopWatchListView;
    Button mStartBtn;
    Button mRecordBtn;
    View mHeader;

    static StopWatchListViewAdapter mStopWatchListViewAdapter;

    static int mStatus;
    static long mBaseTime;
    static long mPauseTime;
    static long mPrevLapTime;
    static String mSavedTime;
    static String mSavedLapTime;

    ArrayList<String[]> mLapRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_watch);
        setup();
        setSavedData();
        setListview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimerHandler.removeMessages(0);
        saveTimesAtPreferences();
    }

    private void setSavedData() {
        // status에 맞게 세팅해주기
        if(mStatus == IDLE) {
            mStartBtn.setText("시작");
            mRecordBtn.setVisibility(View.GONE);
            mTimeTextView.setText("00:00:00");
            mLapTimeTextView.setText("00:00:00");
        } else if(mStatus == RUNNING) {
            setTimesFromPreferences();
            mStartBtn.setText("일시 정지");
            mRecordBtn.setText("기록");

            mRecordBtn.setVisibility(View.VISIBLE);
            mTimerHandler.sendEmptyMessage(0);
            mTimeTextView.setText(getElapsedTime());
            mLapTimeTextView.setText(getLapTime());

            mStopWatchListViewAdapter.notifyDataSetChanged();
        } else if(mStatus == PAUSE) {
            setTimesFromPreferences();
            mStartBtn.setText("계속");
            mRecordBtn.setText("초기화");
            long now = SystemClock.elapsedRealtime();
            mBaseTime += now - mPauseTime;
            mPrevLapTime += now - mPauseTime;
            mPauseTime = now;

            mRecordBtn.setVisibility(View.VISIBLE);
            mTimeTextView.setText(mSavedTime);
            mLapTimeTextView.setText(mSavedLapTime);

            mStopWatchListViewAdapter.notifyDataSetChanged();
        }

    }

    private void setTimesFromPreferences() {
        int length = timesPrefs.getInt("length",0);
        for(int i = 0; i < length; i++) {
            String time = timesPrefs.getString("time_"+i, null);
            String lapTime = timesPrefs.getString("lapTime_"+i, null);
            mLapRecords.add(new String[]{time, lapTime});
        }
    }

    private void saveTimesAtPreferences() {
        int length = mLapRecords.size();
        SharedPreferences.Editor editor = timesPrefs.edit();

        editor.putInt("length", length);
        for(int i = 0; i < length; i++) {
            String time = mLapRecords.get(i)[0];
            String lapTime = mLapRecords.get(i)[1];
            editor.putString("time_"+i, time);
            editor.putString("lapTime_"+i, lapTime);
        }
        editor.apply();
    }

    private void setup() {
        timesPrefs = getSharedPreferences(TIME_PREFS_NAME, MODE_PRIVATE);

        mTimerHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mTimeTextView.setText(getElapsedTime());
                mLapTimeTextView.setText(getLapTime());
                mTimerHandler.sendEmptyMessageDelayed(0, 100);
            }
        };

        mLapRecords = new ArrayList<>();

        mTimeTextView = (TextView) findViewById(R.id.stopWatchTimeTextView);
        mLapTimeTextView = (TextView) findViewById(R.id.stopWatchLapTimeTextView);
        mStopWatchListView = (ListView) findViewById(R.id.stopWatchLapListView);
        mStartBtn = (Button) findViewById(R.id.stopWatchStartBtn);
        mRecordBtn = (Button) findViewById(R.id.stopWatchRecordBtn);
        mHeader = findViewById(R.id.stopWatchHeader);

        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mStatus == IDLE) {
                    // 처음 시작
                    mBaseTime = mPrevLapTime = SystemClock.elapsedRealtime();
                    mTimerHandler.sendEmptyMessage(0);

                    //mTimerHandler.post();
                    mStartBtn.setText("일시 정지");
                    mRecordBtn.setVisibility(View.VISIBLE);
                    mStatus = RUNNING;
                } else if(mStatus == RUNNING) {
                    // 일시정지
                    mPauseTime = SystemClock.elapsedRealtime();
                    mTimerHandler.removeMessages(0);

                    mStartBtn.setText("계속");
                    mRecordBtn.setText("초기화");
                    mStatus = PAUSE;
                } else if(mStatus == PAUSE) {
                    // 다시 재생
                    long now = SystemClock.elapsedRealtime();
                    mBaseTime += (now - mPauseTime);
                    mPrevLapTime += (now - mPauseTime);

                    mTimerHandler.sendEmptyMessage(0);

                    mStartBtn.setText("일시 정지");
                    mRecordBtn.setText("기록");
                    mStatus = RUNNING;
                }
            }
        });

        mRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mStatus == RUNNING) {
                    // 기록하기
                    mLapTimeTextView.setText("00:00:00");
                    mHeader.setVisibility(View.VISIBLE);
                    addLapRecord();
                } else if(mStatus == PAUSE) {
                    // 초기화하기
                    mTimerHandler.removeMessages(0);

                    mTimeTextView.setText("00:00:00");
                    mLapTimeTextView.setText("00:00:00");
                    mStartBtn.setText("시작");
                    mRecordBtn.setText("기록");
                    mRecordBtn.setVisibility(View.GONE);
                    mHeader.setVisibility(View.INVISIBLE);
                    mStatus = IDLE;

                    mLapRecords.clear();
                    mStopWatchListViewAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void setListview() {
        mStopWatchListViewAdapter = new StopWatchListViewAdapter(
                this, R.layout.listview_stop_watch_item, mLapRecords);
        mStopWatchListView.setAdapter(mStopWatchListViewAdapter);
    }

    private String getLapTime() {
        long currentTime = SystemClock.elapsedRealtime();
        long lapTime = currentTime - mPrevLapTime;
        mSavedLapTime = String.format("%02d:%02d:%02d", lapTime / 1000/ 60, (lapTime / 1000) % 60, (lapTime % 1000) / 10);
        return mSavedLapTime;
    }

    private String getElapsedTime() {
        long currentTime = SystemClock.elapsedRealtime();
        long elapsedTime = currentTime - mBaseTime;
        mSavedTime = String.format("%02d:%02d:%02d", elapsedTime / 1000/ 60, (elapsedTime / 1000) % 60, (elapsedTime % 1000) / 10);
        return mSavedTime;
    }

    private void addLapRecord() {
        long currentTime = SystemClock.elapsedRealtime();
        long elapsedTime = currentTime - mBaseTime;
        long elapsedLapTime = currentTime - mPrevLapTime;
        String elapsedTimeText = String.format("%02d:%02d:%02d", elapsedTime / 1000/ 60, (elapsedTime / 1000) % 60, (elapsedTime % 1000) / 10);
        String elapsedLapTimeText = String.format("%02d:%02d:%02d", elapsedLapTime / 1000/ 60, (elapsedLapTime / 1000) % 60, (elapsedLapTime % 1000) / 10);
        String[] addedTimeTexts = new String[2];
        addedTimeTexts[0] = elapsedTimeText;
        addedTimeTexts[1] = elapsedLapTimeText;

        mLapRecords.add(0,addedTimeTexts);

        mPrevLapTime = currentTime;

        mStopWatchListViewAdapter.notifyDataSetChanged();
    }
}
