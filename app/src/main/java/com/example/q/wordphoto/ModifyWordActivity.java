package com.example.q.wordphoto;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ModifyWordActivity extends ChangeItemActivity {
    int index;
    String word;
    String meaning;
    String photoPath;
    String musicPath;

    Button changeBtn;
    EditText meaningView;
    TextView musicTitleTextView;
    ImageButton galleryBtn;
    ImageButton cameraBtn;
    ImageButton searchBtn;
    ImageButton musicBtn;

    String mCurrentMusicPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_word);
        setup();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_SELECT_MUSIC) {
            if (resultCode == RESULT_OK) {
                Uri audioUri = data.getData();
                mCurrentMusicPath = audioUri.toString();

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

    public void setup() {
        Intent modifyWordIntent = getIntent();
        index = modifyWordIntent.getIntExtra("index", 0);
        word = MainActivity.wordItems.get(index).getWord();
        meaning = MainActivity.wordItems.get(index).getMeaning();
        photoPath = MainActivity.wordItems.get(index).getPhotoPaths().get(0);
        musicPath = MainActivity.wordItems.get(index).getMusicPath();

        photo = getBitmap(photoPath);

        wordView = (EditText) findViewById(R.id.modifyWordWordTextView);
        meaningView = (EditText) findViewById(R.id.modifyWordMeaningView);
        galleryBtn = (ImageButton) findViewById(R.id.modifyWordGalleryBtn);
        cameraBtn = (ImageButton) findViewById(R.id.modifyWordCameraBtn);
        searchBtn = (ImageButton) findViewById(R.id.modifyWordSearchBtn);
        musicBtn = (ImageButton) findViewById(R.id.modifyWordMusicBtn);
        musicTitleTextView = (TextView) findViewById(R.id.modifyWordMusicTitleTextView);
        photoView = (ImageView) findViewById(R.id.modifyWordImageView);
        changeBtn = (Button) findViewById(R.id.modifyWordModifyBtn);

        wordView.setText(word);
        meaningView.setText(meaning);

        if(musicPath != null) {
            Uri musicUri = Uri.parse(musicPath);
            String[] proj = {MediaStore.Audio.Media.TITLE};
            Cursor tempCursor = managedQuery(musicUri, proj, null, null, null);
            tempCursor.moveToFirst();
            String musicTitle = tempCursor.getString(tempCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            musicTitleTextView.setText(musicTitle);
        } else {
            musicTitleTextView.setText("추가된 음악이 없습니다.");
        }

        photoView.setImageBitmap(photo);

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhotoFromAlbum();
                Animation animation = AnimationUtils.loadAnimation(ModifyWordActivity.this, R.anim.press_description_word_btn_anim);
                galleryBtn.startAnimation(animation);
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhotoByCamera();
                Animation animation = AnimationUtils.loadAnimation(ModifyWordActivity.this, R.anim.press_description_word_btn_anim);
                cameraBtn.startAnimation(animation);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchPhoto();
                Animation animation = AnimationUtils.loadAnimation(ModifyWordActivity.this, R.anim.press_description_word_btn_anim);
                searchBtn.startAnimation(animation);
            }
        });

        musicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectMusic();
                Animation animation = AnimationUtils.loadAnimation(ModifyWordActivity.this, R.anim.press_description_word_btn_anim);
                musicBtn.startAnimation(animation);
            }
        });
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyWordItem();
            }
        });

    }

    private void modifyWordItem() {
        word = wordView.getText().toString();
        meaning = meaningView.getText().toString();

        MainActivity.wordItems.get(index).setWord(word);
        MainActivity.wordItems.get(index).setMeaning(meaning);
        MainActivity.wordItems.get(index).setPhotoPath(0, mCurrentPhotoPath);
        MainActivity.wordItems.get(index).setMusicPath(mCurrentMusicPath);
        changeBtn.setActivated(false);
        this.finish();
    }

    protected void selectMusic() {
        Intent musicIntent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(musicIntent, REQUEST_SELECT_MUSIC);
    }

    @Override
    public void finish() {
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
        super.finish();
    }
}
