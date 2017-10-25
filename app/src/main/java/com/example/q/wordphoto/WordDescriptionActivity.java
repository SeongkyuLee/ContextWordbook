package com.example.q.wordphoto;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class WordDescriptionActivity extends WordPhotoParentActivity {
    int index;
    TextView musicTitleView;
    TextView musicArtistView;
    ImageView photoView;
    TextView wordView;
    TextView meaningView;
    TextView musicTimeView;
    SeekBar musicSeekBar;
    TextView musicLengthView;
    ImageButton prevBtn;
    ImageButton playBtn;
    ImageButton nextBtn;

    Animation pressBtnAnim;

    static String mSavedTitle = "";
    static int mSavedTime = 0;
    String musicUriPath;
    String title;
    String artist;

    MediaPlayer mMusicPlayer;
    boolean mMusicPlayed;
    Handler mMusicPlayerHandler;
    Thread playMusicThread = new Thread() {
        @Override
        public void run() {
            while(true) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mMusicPlayerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        int second = mMusicPlayer.getCurrentPosition()/1000;
                        String time = String.format("%02d:%02d", second / 60, second % 60);
                        musicTimeView.setText(time);
                        musicSeekBar.setProgress(mMusicPlayer.getCurrentPosition());
                    }
                });

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_description);
        setup();
        setValues();
        playMusicThread.start();
        if(musicUriPath != null && mSavedTitle.equals(title)) {
            mMusicPlayer.seekTo(mSavedTime);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(musicUriPath != null) {
            mSavedTime = mMusicPlayer.getCurrentPosition();
            mSavedTitle = title;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMusicPlayer.stop();
    }

    private void setup() {
        mMusicPlayerHandler = new Handler();
        Intent descriptionIntent = getIntent();
        index = descriptionIntent.getIntExtra("index",0);
        mMusicPlayer = new MediaPlayer();

        pressBtnAnim = AnimationUtils.loadAnimation(WordDescriptionActivity.this, R.anim.press_description_word_btn_anim);

        musicTitleView = (TextView) findViewById(R.id.wordDescriptionMusicTitleView);
        musicArtistView = (TextView) findViewById(R.id.wordDescriptionMusicArtistView);
        photoView = (ImageView) findViewById(R.id.wordDescriptionPhotoView);
        wordView = (TextView) findViewById(R.id.wordDescriptionWordView);
        meaningView = (TextView) findViewById(R.id.wordDescriptionMeaningView);
        musicTimeView = (TextView) findViewById(R.id.wordDescriptionMusicTimeView);
        musicSeekBar = (SeekBar) findViewById(R.id.wordDescriptionMusicSeekBar);
        musicLengthView = (TextView) findViewById(R.id.wordDescriptionMusicLengthView);
        prevBtn = (ImageButton) findViewById(R.id.wordDescriptionPrevBtn);
        playBtn = (ImageButton) findViewById(R.id.wordDescriptionPlayBtn);
        nextBtn = (ImageButton) findViewById(R.id.wordDescriptionNextBtn);

        mMusicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(musicUriPath!= null)
                    showNextWord();
            }
        });
        musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(seekBar.getMax() == progress) {
                    mMusicPlayer.stop();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mMusicPlayed = mMusicPlayer.isPlaying();
                mMusicPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int position = seekBar.getProgress();
                mMusicPlayer.seekTo(position);
                if(mMusicPlayed) {
                    mMusicPlayer.start();
                }
            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevBtn.startAnimation(pressBtnAnim);

                showPrevWord();
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playBtn.startAnimation(pressBtnAnim);
                playMusic();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextBtn.startAnimation(pressBtnAnim);
                showNextWord();
            }
        });
    }

    private void setValues() {
        playBtn.setActivated(true);

        String word = MainActivity.wordItems.get(index).getWord();
        String meaning = MainActivity.wordItems.get(index).getMeaning();
        String photoPath = MainActivity.wordItems.get(index).getPhotoPaths().get(0);
        musicUriPath = MainActivity.wordItems.get(index).getMusicPath();
        Bitmap photo = getBitmap(photoPath);

        if(musicUriPath == null) {
            musicTitleView.setText("등록된 음악이 없습니다.");
            musicArtistView.setText("등록된 아티스트 없습니다.");
            musicLengthView.setText("00:00");
        } else {
            // 곡 이름과 타이틀을 등록해준다.
            Uri myUri = Uri.parse(musicUriPath);
            String[] proj = {MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST};
            Cursor tempCursor = managedQuery(myUri, proj, null, null, null);
            tempCursor.moveToFirst();
            title = tempCursor.getString(tempCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            artist = tempCursor.getString(tempCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            musicTitleView.setText(title);
            musicArtistView.setText(artist);

            // 음악을 로드한다.
            loadMusic();
            int musicLength = mMusicPlayer.getDuration();
            musicSeekBar.setMax(musicLength);
            String time = String.format("%02d:%02d", (musicLength/1000) / 60, (musicLength/1000) % 60);
            musicLengthView.setText(time);
        }
        if(photoPath == null) {
            photoView.setImageResource(R.color.colorGray);
        } else {
            photoView.setImageBitmap(photo);
        }

        wordView.setText(word);
        meaningView.setText(meaning);
    }
    private void loadMusic() {
        try {
            Uri musicUri = Uri.parse(musicUriPath);
            mMusicPlayer.setDataSource(this, musicUri);
            mMusicPlayer.prepare();
            //start 후 pause를 해줘야 seek bar를 드로그 했을 때 제대로 동작한다
            mMusicPlayer.start();
            mMusicPlayer.pause();
        } catch (IOException e) {
            Toast.makeText(this, "음악을 로드하는데 실패 했습니다", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }
    private void showPrevWord() {
        if (index == 0) {
            Toast.makeText(this, "처음 단어입니다.", Toast.LENGTH_SHORT).show();
        } else {
            boolean wasPlayed = mMusicPlayer.isPlaying();
            index--;
            playBtn.setActivated(true);
            playBtn.setImageResource(android.R.drawable.ic_media_play);
            mMusicPlayer.reset();
            musicSeekBar.setProgress(0);
            setValues();
            if(musicUriPath != null && wasPlayed) {
                playMusic();
            }
        }
    }

    private void showNextWord() {
        if (index == MainActivity.wordItems.size() - 1) {
            Toast.makeText(this, "마지막 단어입니다", Toast.LENGTH_SHORT).show();
        } else {
            boolean wasPlayed = mMusicPlayer.isPlaying();
            index++;
            playBtn.setActivated(true);
            playBtn.setImageResource(android.R.drawable.ic_media_play);
            mMusicPlayer.reset();
            musicSeekBar.setProgress(0);
            setValues();
            if(musicUriPath != null && wasPlayed) {
                playMusic();
            }
        }
    }

    private void playMusic() {
        if(musicUriPath == null) {
            Toast.makeText(this, "등록된 음악이 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            if (playBtn.isActivated()) {
                mMusicPlayer.start();
                playBtn.setActivated(false);
                playBtn.setImageResource(android.R.drawable.ic_media_pause);
            } else {
                mMusicPlayer.pause();
                playBtn.setActivated(true);
                playBtn.setImageResource(android.R.drawable.ic_media_play);
            }
        }

    }

    private Bitmap getBitmap(String photoPath) {
        Bitmap tempPhoto = null;

        if(photoPath == null)
            return null;

        try {
            Uri imageUri = Uri.parse(photoPath);
            InputStream imageStream = null;
            imageStream = getContentResolver().openInputStream(imageUri);
            tempPhoto = BitmapFactory.decodeStream(imageStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tempPhoto;
    }
}
