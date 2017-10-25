package com.example.q.wordphoto;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class NewsActivity extends WordPhotoParentActivity {
    NaverNews mNaverNews;
    ArrayList<News> mNewsItems;
    int mDisplayNum = 10;
    int mIndex;
    String mWord;
    String mMeaning;
    boolean mIsRunning;
    boolean mIsChanged;

    TextView mNewsWordView;
    TextView mNewsMeaningView;
    ListView mNewsListView;
    Button mNewsPrevBtn;
    Button mNewsNextBtn;

    NewsListViewAdapter mNewsListViewAdapter;

    AsyncTask<Integer,News,Integer> asyncTask = new AsyncTask<Integer, News, Integer>() {
        @Override
        protected Integer doInBackground(Integer... integers) {
            while(mIsRunning) {
                if(mIsChanged) {
                    try {
                        ArrayList<News> tempNewsItems = mNaverNews.getNews(mWord, mDisplayNum);
                        for(int i = 0; i < mDisplayNum;i++) {
                            if(mNewsItems.size() < mDisplayNum) {
                                mNewsItems.add(tempNewsItems.get(i));
                            } else {
                                mNewsItems.set(i, tempNewsItems.get(i));
                            }

                        }
                        publishProgress();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mIsChanged = false;
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(News... values) {
            mNewsListViewAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsRunning = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Intent intent = getIntent();
        mIndex = intent.getIntExtra("index",0);
        mNewsItems = new ArrayList<>();
        mNaverNews = new NaverNews();

        findView();
        setListView();
        setOnClicks();
        setValues();
        mIsRunning = true;
        mIsChanged = true;
        asyncTask.execute(0);
    }

    private void findView() {
        mNewsWordView = (TextView) findViewById(R.id.naverNewsWordView);
        mNewsMeaningView = (TextView) findViewById(R.id.naverNewsMeaningView);
        mNewsListView = (ListView) findViewById(R.id.naverNewsListView);
        mNewsPrevBtn = (Button) findViewById(R.id.naverNewsPrevBtn);
        mNewsNextBtn = (Button) findViewById(R.id.naverNewsNextBtn);
    }

    private void setListView() {
        mNewsListViewAdapter = new NewsListViewAdapter(NewsActivity.this, R.layout.listview_news_item, mNewsItems);
        mNewsListView.setAdapter(mNewsListViewAdapter);
    }

    private void setValues() {
        mWord = MainActivity.wordItems.get(mIndex).getWord();
        mMeaning = MainActivity.wordItems.get(mIndex).getMeaning();
        mIsChanged = true;

        setText();
    }

    private void setText() {
        mNewsWordView.setText(mWord);
        mNewsMeaningView.setText(mMeaning);
    }

    private void setOnClicks() {
        mNewsPrevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPrevWord();
            }
        });
        mNewsNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNextWord();
            }
        });
    }

    private void showPrevWord() {
        if (mIndex == 0) {
            Toast.makeText(this, "처음 단어입니다.", Toast.LENGTH_SHORT).show();
        } else {
            mIndex--;
            setValues();
        }
    }

    private void showNextWord() {
        if (mIndex == MainActivity.wordItems.size() - 1) {
            Toast.makeText(this, "마지막 단어입니다", Toast.LENGTH_SHORT).show();
        } else {
            mIndex++;
            setValues();
        }
    }

}
