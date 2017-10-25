package com.example.q.wordphoto;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class AddWordActivity extends ChangeItemActivity {
    static String savedWord = null;
    static String savedMeaning = null;
    static String savedPhotoPath = null;

    Button addBtn;
    EditText meaningView;
    TextView musicTitleTextView;
    ImageButton cameraBtn;
    ImageButton galleryBtn;
    ImageButton searchBtn;
    ImageButton musicBtn;

    String mCurrentMusicTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);
        setup();
        recoveryData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_SELECT_MUSIC) {
            if (resultCode == RESULT_OK) {
                Uri audioUri = data.getData();
                mCurrentMusicTitle = audioUri.toString();

                String[] proj = {MediaStore.Audio.Media.TITLE};
                Cursor tempCursor = managedQuery(audioUri, proj, null, null, null);
                tempCursor.moveToFirst();
                String title = tempCursor.getString(tempCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                musicTitleTextView.setText("음악 : " + title);
            } else {
                Toast.makeText(this, "음악을 선택하지 않았습니다.",Toast.LENGTH_SHORT).show();
            }

        }
    }

    // 사용자가 뒤로 가기를 눌러도 데이터를 남아 있게 한다.
    private void recoveryData() {
        if (savedWord != null) {
            wordView.setText(savedWord);
            savedWord = null;
        }
        if(savedMeaning != null) {
            meaningView.setText(savedMeaning);
            savedMeaning = null;
        }
        if(savedPhotoPath != null) {
            photo = getBitmap(savedPhotoPath);
            photoView.setImageBitmap(photo);
            savedPhotoPath = null;
        }
    }

    private void saveData() {
        if (addBtn.isActivated()) {
            savedWord = wordView.getText().toString();
            savedMeaning = meaningView.getText().toString();
            savedPhotoPath = mCurrentPhotoPath;
        } else {
            savedWord = null;
            savedMeaning = null;
            savedPhotoPath = null;
        }
    }

    public void setup() {
        wordView = (EditText) findViewById(R.id.addWordWordTextView);
        meaningView = (EditText) findViewById(R.id.addWordMeaningView);
        cameraBtn = (ImageButton) findViewById(R.id.addWordCameraBtn);
        galleryBtn = (ImageButton) findViewById(R.id.addWordGalleryBtn);
        searchBtn = (ImageButton) findViewById(R.id.addWordSearchBtn);
        musicBtn = (ImageButton) findViewById(R.id.addWordMusicBtn);
        musicTitleTextView = (TextView) findViewById(R.id.addWordMusicTitleTextView);
        photoView = (ImageView) findViewById(R.id.addWordImageView);
        addBtn = (Button) findViewById(R.id.addWordAddBtn);
        addBtn.setActivated(true);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addWordItem();
            }
        });
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhotoFromAlbum();
                Animation animation = AnimationUtils.loadAnimation(AddWordActivity.this, R.anim.press_description_word_btn_anim);
                galleryBtn.startAnimation(animation);
            }
        });
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhotoByCamera();
                Animation animation = AnimationUtils.loadAnimation(AddWordActivity.this, R.anim.press_description_word_btn_anim);
                cameraBtn.startAnimation(animation);
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchPhoto();
                Animation animation = AnimationUtils.loadAnimation(AddWordActivity.this, R.anim.press_description_word_btn_anim);
                searchBtn.startAnimation(animation);
            }
        });
        musicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectMusic();
                Animation animation = AnimationUtils.loadAnimation(AddWordActivity.this, R.anim.press_description_word_btn_anim);
                musicBtn.startAnimation(animation);
            }
        });
    }

    // add method
    public void addWordItem() {
        if (addBtn.isActivated()) {
            if(wordView.getText().toString().equals("") & meaningView.getText().toString().equals("")) {
                Toast.makeText(this, "단어와 의미를 입력해주세요", Toast.LENGTH_SHORT).show();
            } else if(wordView.getText().toString().equals("")) {
                Toast.makeText(this, "단어를 입력해주세요", Toast.LENGTH_SHORT).show();
            } else if(meaningView.getText().toString().equals("")) {
                Toast.makeText(this, "의미를 입력해주세요", Toast.LENGTH_SHORT).show();
            } else {
                String addedWord = wordView.getText().toString();
                String addedMeaning = meaningView.getText().toString();
                String addedPhotoPath;
                String addedMusicTitle = mCurrentMusicTitle;

                if(photoView.getDrawable() != null) {
                    addedPhotoPath = mCurrentPhotoPath;
                } else {
                    addedPhotoPath = null;
                }

                WordItem addedWordItem = new WordItem(addedWord, addedMeaning, addedPhotoPath);
                addedWordItem.setMusicPath(addedMusicTitle);
                MainActivity.wordItems.add(0,addedWordItem);
                addBtn.setActivated(false);
                this.finish();
            }
        }
    }

    protected void selectMusic() {
        Intent musicIntent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(musicIntent, REQUEST_SELECT_MUSIC);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void finish() {
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
        super.finish();
    }
}
