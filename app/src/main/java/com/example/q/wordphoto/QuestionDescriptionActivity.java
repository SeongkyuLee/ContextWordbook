package com.example.q.wordphoto;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class QuestionDescriptionActivity extends WordPhotoParentActivity {
    public static ArrayList<ReplyItem> replyItems;
    public static final String REPLIES_PREFS_NAME = "ReplyItems";
    SharedPreferences replyPreferences;
    int index;
    String photoPath;
    String word;
    String meaning;
    String writer;
    String date;
    String content;
    int viewNum;
    int replyNum;

    ImageView photoView;
    TextView wordView;
    TextView meaningView;
    TextView writerView;
    TextView dateView;
    TextView viewNumView;
    TextView contentView;
    TextView replyNumView;
    EditText replyContentEditText;
    Button addReplyBtn;

    ReplyListViewAdapter replyListViewAdapter;
    ListView replyListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_description);
        setup();
        replyPreferences = getSharedPreferences(REPLIES_PREFS_NAME, MODE_PRIVATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveReplyItemsAtPreferences();
    }

    private void saveReplyItemsAtPreferences() {
        int length = QuestionActivity.replyItemsOfItems.size();
        SharedPreferences.Editor editor = replyPreferences.edit();

        // 전체 몇 개의 질문이 있는지 저장을 한다
        editor.putInt("length",length);

        for(int i = 0;i < length; i++) {
            // 질문에 해당하는 댓글 꾸러미들의 아이디를 question_0, question_1 등으로 이름 붙임
            String questionId = "question" + "_" + i;
            ArrayList<ReplyItem> replyItems = QuestionActivity.replyItemsOfItems.get(i);
            int replyLength = replyItems.size();
            editor.putInt(questionId, replyLength);

            for(int j = 0;j < replyLength; j++) {
                // 각 질문 안에서 댓글에 해당하는 부분들
                ReplyItem replyItem = replyItems.get(j);
                String replyItemId = i + "_" + j;
                String writer = replyItem.getWriter();
                String content = replyItem.getContent();
                String date = replyItem.getDate();

                editor.putString(replyItemId + "_writer", writer);
                editor.putString(replyItemId + "_content", content);
                editor.putString(replyItemId + "_date", date);
            }
        }

        editor.apply();

        Log.d("Reply shared preference", "Save preferences");
    }
    public void setup() {
        Intent questionDescriptionIntent = getIntent();
        index = questionDescriptionIntent.getIntExtra("index",0);
        replyItems = QuestionActivity.replyItemsOfItems.get(index);

        setListView();

        photoPath = QuestionActivity.questionItems.get(index).getPhotoPath();
        word = QuestionActivity.questionItems.get(index).getWord();
        meaning = QuestionActivity.questionItems.get(index).getMeaning();
        writer = QuestionActivity.questionItems.get(index).getWriter();
        date = QuestionActivity.questionItems.get(index).getDate();
        content = QuestionActivity.questionItems.get(index).getContent();
        viewNum = QuestionActivity.questionItems.get(index).getViewNum();
        replyNum = QuestionActivity.questionItems.get(index).getReplyNum();

        photoView = (ImageView) findViewById(R.id.questionDescriptionImageView);
        wordView = (TextView) findViewById(R.id.questionDescriptionWordView);
        meaningView = (TextView) findViewById(R.id.questionDescriptionMeaningView);
        writerView = (TextView) findViewById(R.id.questionDescriptionWriterView);
        dateView = (TextView) findViewById(R.id.questionDescriptionDateView);
        viewNumView = (TextView) findViewById(R.id.questionDescriptionViewNumView);
        contentView = (TextView) findViewById(R.id.questionDescriptionContentView);
        replyNumView = (TextView) findViewById(R.id.questionDescriptionReplyNumView);
        addReplyBtn = (Button) findViewById(R.id.questionDescriptionAddReplyBtn);
        replyContentEditText = (EditText) findViewById(R.id.questionDescriptionReplyEditTextView);

        photoView.setImageBitmap(getBitmap(photoPath));
        wordView.setText(word);
        meaningView.setText(meaning);
        writerView.setText(writer);
        dateView.setText(date);
        viewNumView.setText(Integer.toString(viewNum));
        contentView.setText(content);
        replyNumView.setText(Integer.toString(replyNum));

        addReplyBtn.setOnClickListener(new View.OnClickListener() {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            Animation pressAddReplyBtnAnim = AnimationUtils.loadAnimation(QuestionDescriptionActivity.this, R.anim.press_description_word_btn_anim);
            @Override
            public void onClick(View view) {
                addReplyBtn.startAnimation(pressAddReplyBtnAnim);
                addReply();
                imm.hideSoftInputFromWindow(replyContentEditText.getWindowToken(), 0);
            }
        });
    }

    public void setListView() {
        //inflate view
        View header = getLayoutInflater().inflate(R.layout.header_question_description, null, false);

        replyListView = (ListView) findViewById(R.id.questionDescriptionReplyListView);
        replyListView.addHeaderView(header);
        replyListViewAdapter = new ReplyListViewAdapter(this, R.layout.listview_reply_item, replyItems);
        replyListView.setAdapter(replyListViewAdapter);
    }

    public void addReply() {
        String writer = MainActivity.id;
        String replyContent = replyContentEditText.getText().toString();
        if(!replyContent.equals("")) {
            ReplyItem addedReplyItem = new ReplyItem(writer, replyContent);
            replyItems.add(0, addedReplyItem);
            replyNumView.setText(Integer.toString(replyItems.size()));
            replyContentEditText.setText("");
            replyListViewAdapter.notifyDataSetChanged();
        }
    }

    public void modifyReply(int index) {

        if(replyItems.get(index).getWriter().equals(MainActivity.id)) {
            final int finalIndex = index;
            ReplyItem modifiedReplyItem = replyItems.get(finalIndex);

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            final EditText content = new EditText(this);
            content.setText(modifiedReplyItem.getContent());
            alert.setView(content);


            alert.setPositiveButton("수정", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String modifiedContent = content.getText().toString();
                    replyItems.get(finalIndex).setContent(modifiedContent);

                    /*
                    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA);
                    Date currentTime = new Date();
                    String replyDate = format.format(currentTime);
                    replyItems.get(finalIndex).setDate(replyDate);
                    */

                    replyListViewAdapter.notifyDataSetChanged();
                }
            });

            alert.setNegativeButton("취소",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            alert.show();
        } else {
            Toast.makeText(QuestionDescriptionActivity.this, "다른 사람의 댓글을 수정 할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteReply(int index) {
        // 팝업 켜기
        if(replyItems.get(index).getWriter().equals(MainActivity.id)) {
            final int finalIndex = index;

            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setMessage("댓글을 삭제 하시겠습니까?");

            alert.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    replyItems.remove(finalIndex);
                    replyNumView.setText(Integer.toString(replyItems.size()));
                    replyListViewAdapter.notifyDataSetChanged();
                }
            });

            alert.setNegativeButton("취소",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });

            alert.show();

        } else {
            Toast.makeText(QuestionDescriptionActivity.this, "다른 사람의 댓글을 삭제 할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }

    }

    protected Bitmap getBitmap(String photoPath) {
        Bitmap tempPhoto = null;

        if(photoPath == null)
            return null;

        try {
            Uri imageUri = Uri.parse(photoPath);
            InputStream imageStream = null;
            imageStream = this.getContentResolver().openInputStream(imageUri);
            tempPhoto = BitmapFactory.decodeStream(imageStream);
            tempPhoto = Bitmap.createScaledBitmap(tempPhoto, 150, 150, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tempPhoto;
    }
}
