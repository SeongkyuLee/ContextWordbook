package com.example.q.wordphoto;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ModifyQuestionActivity extends ChangeItemActivity {
    int index;
    String word;
    String meaning;
    String content;
    String photoPath;

    Button modifyBtn;
    EditText meaningView;
    EditText contentView;
    ImageButton galleryBtn;
    ImageButton cameraBtn;
    ImageButton searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_question);
        setup();
    }

    private void setup() {
        Intent modifyIntent = getIntent();
        index = modifyIntent.getIntExtra("index",0);
        word = QuestionActivity.questionItems.get(index).getWord();
        meaning = QuestionActivity.questionItems.get(index).getMeaning();
        content = QuestionActivity.questionItems.get(index).getContent();
        photoPath = QuestionActivity.questionItems.get(index).getPhotoPath();

        wordView = (EditText) findViewById(R.id.modifyQuestionWordTextView);
        meaningView = (EditText) findViewById(R.id.modifyQuestionTitleView);
        contentView = (EditText) findViewById(R.id.modifyQuestionContentView);
        galleryBtn = (ImageButton) findViewById(R.id.modifyQuestionGalleryBtn);
        cameraBtn = (ImageButton) findViewById(R.id.modifyQuestionCameraBtn);
        searchBtn = (ImageButton) findViewById(R.id.modifyQuestionSearchBtn);
        photoView = (ImageView) findViewById(R.id.modifyQuestionImageView);
        photoView = (ImageView) findViewById(R.id.modifyQuestionImageView);
        modifyBtn = (Button) findViewById(R.id.modifyQuestionModifyBtn);

        wordView.setText(word);
        meaningView.setText(meaning);
        contentView.setText(content);
        photoView.setImageBitmap(getBitmap(photoPath));

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhotoFromAlbum();
                Animation animation = AnimationUtils.loadAnimation(ModifyQuestionActivity.this, R.anim.press_description_word_btn_anim);
                galleryBtn.startAnimation(animation);
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhotoByCamera();
                Animation animation = AnimationUtils.loadAnimation(ModifyQuestionActivity.this, R.anim.press_description_word_btn_anim);
                cameraBtn.startAnimation(animation);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchPhoto();
                Animation animation = AnimationUtils.loadAnimation(ModifyQuestionActivity.this, R.anim.press_description_word_btn_anim);
                searchBtn.startAnimation(animation);
            }
        });

        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyQuestionItem();
                Animation animation = AnimationUtils.loadAnimation(ModifyQuestionActivity.this, R.anim.press_description_word_btn_anim);
                modifyBtn.startAnimation(animation);
            }
        });
    }

    private void modifyQuestionItem() {
        word = wordView.getText().toString();
        meaning = meaningView.getText().toString();
        content = contentView.getText().toString();
        photo = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
        photoPath = mCurrentPhotoPath;

        QuestionActivity.questionItems.get(index).setWord(word);
        QuestionActivity.questionItems.get(index).setMeaning(meaning);
        QuestionActivity.questionItems.get(index).setContent(content);
        QuestionActivity.questionItems.get(index).setPhotoPath(photoPath);
        this.finish();
    }

    @Override
    public void finish() {
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
        super.finish();
    }
}
