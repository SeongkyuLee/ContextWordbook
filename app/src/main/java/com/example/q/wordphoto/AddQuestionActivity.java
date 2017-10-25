package com.example.q.wordphoto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddQuestionActivity extends ChangeItemActivity {
    static String savedWord = null;
    static String savedMeaning = null;
    static String savedContent = null;
    static String savedPhotoPath = null;

    EditText meaningView;
    EditText contentView;
    ImageButton cameraBtn;
    ImageButton galleryBtn;
    ImageButton searchBtn;
    Button addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        setup();
        recoveryData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveData();
    }

    // 사용자가 뒤로 가기를 눌러도 데이터를 남아 있게 한다.
    private void recoveryData() {
        if (savedWord != null) {
            wordView.setText(savedWord);
            savedWord = null;
        }
        if(savedMeaning != null) {
            contentView.setText(savedMeaning);
            savedMeaning = null;
        }
        if(savedContent != null) {
            contentView.setText(savedContent);
            savedContent = null;
        }
        if(savedPhotoPath != null) {
            photo = getBitmap(savedPhotoPath);
            photoView.setImageBitmap(photo);
            savedPhotoPath = null;
        }
    }

    private void saveData() {
        //temp photo는 on activity result에서 구현
        if (addBtn.isActivated()) {
            savedWord = wordView.getText().toString();
            savedMeaning = meaningView.getText().toString();
            savedContent = contentView.getText().toString();
            savedPhotoPath = mCurrentPhotoPath;
        } else {
            savedWord = null;
            savedMeaning = null;
            savedContent = null;
            savedPhotoPath = null;
        }
    }

    private void setup() {
        wordView = (EditText) findViewById(R.id.addQuestionWordTextView);
        meaningView = (EditText) findViewById(R.id.addQuestionMeaningView);
        contentView = (EditText) findViewById(R.id.addQuestionContentView);
        cameraBtn = (ImageButton) findViewById(R.id.addQuestionCameraBtn);
        galleryBtn = (ImageButton) findViewById(R.id.addQuestionGalleryBtn);
        searchBtn = (ImageButton) findViewById(R.id.addQuestionSearchBtn);
        photoView = (ImageView) findViewById(R.id.addQuestionImageView);
        addBtn = (Button) findViewById(R.id.addQuestionAddBtn);
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
                Animation animation = AnimationUtils.loadAnimation(AddQuestionActivity.this, R.anim.press_description_word_btn_anim);
                galleryBtn.startAnimation(animation);
            }
        });
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhotoByCamera();
                Animation animation = AnimationUtils.loadAnimation(AddQuestionActivity.this, R.anim.press_description_word_btn_anim);
                cameraBtn.startAnimation(animation);
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchPhoto();
                Animation animation = AnimationUtils.loadAnimation(AddQuestionActivity.this, R.anim.press_description_word_btn_anim);
                searchBtn.startAnimation(animation);
            }
        });
    }





    // add method
    public void addWordItem() {
        if (addBtn.isActivated()) {
            if(wordView.getText().toString().equals("")
                    && meaningView.getText().toString().equals("")&& meaningView.getText().toString().equals("")
                    && contentView.getText().toString().equals("")) {
                Toast.makeText(this, "단어, 제목 내용을 입력해주세요", Toast.LENGTH_SHORT).show();
            } else if(wordView.getText().toString().equals("")) {
                Toast.makeText(this, "단어를 입력해주세요", Toast.LENGTH_SHORT).show();
            } else if(meaningView.getText().toString().equals("")) {
                Toast.makeText(this, "질문 제목을 입력해주세요", Toast.LENGTH_SHORT).show();
            } else if(contentView.getText().toString().equals("")) {
                Toast.makeText(this, "질문 내용을 입력해주세요", Toast.LENGTH_SHORT).show();
            } else {
                String addedWord = wordView.getText().toString();
                String addedMeaning = meaningView.getText().toString();
                String addedContent = contentView.getText().toString();
                String addedPhotoPath;

                if(photoView.getDrawable() != null) {
                    addedPhotoPath = mCurrentPhotoPath;
                } else {
                    addedPhotoPath = null;
                }

                QuestionItem addedQuestionItem = new QuestionItem(addedWord, addedMeaning,
                        addedContent, MainActivity.id, addedPhotoPath);

                QuestionActivity.questionItems.add(0,addedQuestionItem);
                QuestionActivity.replyItemsOfItems.add(0,new ArrayList<ReplyItem>());
                QuestionActivity.questionItems.get(0).setReply(QuestionActivity.replyItemsOfItems.get(0));
                addBtn.setActivated(false);
                this.finish();
            }
        }
    }

    @Override
    public void finish() {
        startActivity(new Intent(AddQuestionActivity.this, QuestionActivity.class));
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
        super.finish();
    }
}
