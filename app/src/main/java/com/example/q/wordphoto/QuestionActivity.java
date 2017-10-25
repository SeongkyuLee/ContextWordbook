package com.example.q.wordphoto;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class QuestionActivity extends WordPhotoParentActivity {
    public static final String QUESTIONS_PREFS_NAME = "QuestionItems";
    public static final String REPLIES_PREFS_NAME = "ReplyItems";
    SharedPreferences questionPreferences;
    SharedPreferences replyPreferences;

    public static int visitNumber = 0;
    ListView questionListView;
    QuestionListViewAdapter questionListViewAdapter;
    public static ArrayList<QuestionItem> questionItems = new ArrayList<QuestionItem>();
    public static ArrayList<ArrayList<ReplyItem>> replyItemsOfItems = new ArrayList<ArrayList<ReplyItem>>();
    Button addQuestionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        replyPreferences = getSharedPreferences(REPLIES_PREFS_NAME, MODE_PRIVATE);
        questionPreferences = getSharedPreferences(QUESTIONS_PREFS_NAME, MODE_PRIVATE);
        setView();
        if(MainActivity.tested) {
            if(visitNumber == 0) {
                //setTestData();
                setReplyItemsFromPreferences();
                setQuestionItemsFromPreferences();

                visitNumber++;
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(questionListViewAdapter != null)
            questionListViewAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onStop() {
        super.onStop();
        saveQuestionItemsAtPreferences();
        saveReplyItemsAtPreferences();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private  void setTestData() {
        questionItems.add(new QuestionItem("down", "down이 좋은 뜻인가요?",
                "#일상 영어 \n" +
                        "안녕하세요 저는 현재 미국에서 공부하고 있는 학생입니다.\n" +
                        "친구들이랑 이야기 하다 보면 자주 'I'm down for it'이란 말을 합니다.\n" +
                        "down이 우울할 때 사용하는 단어 아닌가요? \n" +
                        "이럴 때는 뭐라고 대답을 해줘야 하나요?\n",
                "engStudent"));
        questionItems.add(new QuestionItem("thread", "thread가 뭔가요?",
                "#전문 용어 \n" +
                        "thread가 뭔가요?\n" +
                        "자바에서 중요한 것 같은데 사전에서 찾아봐도 실타래라는 뜻 밖에 안 나오네요.\n" +
                        "도저 무슨 뜻인지 감이 안 오네요.\n",
                "javaStudent"));

        for (int i = 0; i < questionItems.size();i++) {
            replyItemsOfItems.add(new ArrayList<ReplyItem>());
        }

        replyItemsOfItems.get(0).add(0,new ReplyItem("영어고수","I'm down for it은 좋은 뜻입니다. " +
                "나도 그것을 하고 싶다 또는 가고 싶다 이런 뜻입니다. " +
                "글쓴 분께서도 친구들이 밥 먹으러 가자고 하면 I'm down for it이란 표현을 써보도록 하세요."));
        replyItemsOfItems.get(0).add(0,new ReplyItem("지나가던행인","아래에 써주신 분 설명도 맞지만..." +
                "I feel down이라고 쓰는 경우에는 우울하다는 뜻입니다." +
                "노파심에 글 남기고 갑니다..." +
                "이만.."));

        replyItemsOfItems.get(1).add(0, new ReplyItem("gosu","쓰레드는 하나의 흐름이라고 생각하시면 됩니다."));
        replyItemsOfItems.get(1).add(0, new ReplyItem("안녕","아랫분 말씀처럼 쓰레드는 흐름을 뜻합니다." +
                "흐름이 한 개라면 싱글 스레드, 흐름이 여러개면 멀티 스레드인 것입니다."));

        questionItems.get(0).increaseView();
        questionItems.get(0).increaseView();
        questionItems.get(1).increaseView();
        questionItems.get(1).increaseView();

        questionItems.get(0).setReply(replyItemsOfItems.get(0));
        questionItems.get(1).setReply(replyItemsOfItems.get(1));
        questionListViewAdapter.notifyDataSetChanged();
    }

    private void setQuestionItemsFromPreferences() {
        int length = questionPreferences.getInt("length",0);
        for(int i = 0; i < length; i++) {
            String word = questionPreferences.getString(i+"_word", null);
            String title = questionPreferences.getString(i+"_title", null);
            String writer = questionPreferences.getString(i+"_writer", null);
            String content = questionPreferences.getString(i+"_content", null);
            String photoPath = questionPreferences.getString(i+"_photoPath", null);
            String date = questionPreferences.getString(i+"_date",null);
            int viewNum = questionPreferences.getInt(i+"_viewNum",0);
            ArrayList<ReplyItem> replyItems = replyItemsOfItems.get(i);

            QuestionItem questionItem = new QuestionItem(word, title, content, writer, photoPath);
            questionItem.setDate(date);
            questionItem.setViewNum(viewNum);
            questionItem.setReply(replyItems);

            questionItems.add(questionItem);
        }
        Log.d("Q shared preference", "Set preferences");
    }

    private void saveQuestionItemsAtPreferences() {
        int length = questionItems.size();
        SharedPreferences.Editor editor = questionPreferences.edit();

        for(int i = 0; i < length; i++) {
            String word = questionItems.get(i).getWord();
            String title = questionItems.get(i).getMeaning();
            String writer = questionItems.get(i).getWriter();
            String content = questionItems.get(i).getContent();
            String photoPath = questionItems.get(i).getPhotoPath();
            String date = questionItems.get(i).getDate();
            int viewNum = questionItems.get(i).getViewNum();

            editor.putString(i + "_word", word);
            editor.putString(i + "_title", title);
            editor.putString(i + "_writer", writer);
            editor.putString(i + "_content", content);
            editor.putString(i + "_photoPath", photoPath);
            editor.putString(i + "_date", date);
            editor.putInt(i + "_viewNum", viewNum);
        }

        editor.putInt("length", length);
        editor.apply();

        Log.d("Q shared preference", "Save preferences");
    }

    private void setReplyItemsFromPreferences() {
        // 전체 댓글 꾸러미의 개수 = 전체 글의 개수
        int length = replyPreferences.getInt("length",0);
        for(int i = 0; i < length; i++) {
            ArrayList<ReplyItem> replyItems = new ArrayList<>();
            String questionId = "question" + "_" + i;
            int replyLength = replyPreferences.getInt(questionId,0);

            for(int j = 0; j < replyLength; j++) {
                String replyItemId = i + "_" + j;
                String writer = replyPreferences.getString(replyItemId + "_writer", null);
                String content = replyPreferences.getString(replyItemId + "_content", null);
                String date = replyPreferences.getString(replyItemId + "_date", null);
                ReplyItem replyItem = new ReplyItem(writer, content, date);

                replyItems.add(j, replyItem);
            }
            if(replyItemsOfItems.size() <= length) {
                replyItemsOfItems.add(i, replyItems);
            } else {
                replyItemsOfItems.set(i, replyItems);
            }
        }
        Log.d("Reply shared preference", "Set preferences");
    }

    private void saveReplyItemsAtPreferences() {
        int length = replyItemsOfItems.size();
        SharedPreferences.Editor editor = replyPreferences.edit();

        // 전체 몇 개의 질문이 있는지 저장을 한다
        editor.putInt("length",length);

        for(int i = 0;i < length; i++) {
            // 질문에 해당하는 댓글 꾸러미들의 아이디를 question_0, question_1 등으로 이름 붙임
            String questionId = "question" + "_" + i;
            ArrayList<ReplyItem> replyItems = replyItemsOfItems.get(i);
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
    private void setView() {
        setListView();
        addQuestionBtn = (Button) findViewById(R.id.askQuestionAskBtn);
        addQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callAddQuestion();
            }
        });
    }

    private void setListView() {
        questionListView = (ListView) findViewById(R.id.askQuestionListView);
        questionListViewAdapter =
                new QuestionListViewAdapter(QuestionActivity.this,
                        R.layout.listview_question_item, questionItems);
        questionListView.setAdapter(questionListViewAdapter);
        questionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                questionItems.get(position).increaseView();
                callQuestionDescription(position);
            }
        });

        //팝업창 띄우기
        questionListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int index = position;
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setMessage("본인이 쓴 글만 수정, 삭제 할 수 있습니다.");

                alert.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(questionItems.get(index).getWriter().equals(MainActivity.id)) {
                            deleteQuestionItem(index);
                        } else {

                            Toast.makeText(QuestionActivity.this, "다른 사람의 글을 지울 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                alert.setNegativeButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(questionItems.get(index).getWriter().equals(MainActivity.id)) {
                            modifyQuestionItem(index);
                        } else {
                            Toast.makeText(QuestionActivity.this, "다른 사람의 글을 수정 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                alert.setNeutralButton("내 단어장에 추가", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addQuestionItemToMine(index);
                    }
                });
                alert.show();

                return true;
            };
        });
    }

    private void callQuestionDescription(int position) {
        Intent questionDescriptionIntent = new Intent(this, QuestionDescriptionActivity.class);
        questionDescriptionIntent.putExtra("index", position);
        startActivity(questionDescriptionIntent);
    }

    private void callAddQuestion() {
        Intent addQuestionIntent = new Intent(this, AddQuestionActivity.class);
        startActivity(addQuestionIntent);
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_top);
    }

    private void deleteQuestionItem(int position) {
        questionItems.remove(position);
        replyItemsOfItems.remove(position);
        questionListViewAdapter.notifyDataSetChanged();
    }

    private void modifyQuestionItem(int position) {
        Intent modifyQuestionIntent = new Intent(QuestionActivity.this, ModifyQuestionActivity.class);
        modifyQuestionIntent.putExtra("index", position);
        startActivity(modifyQuestionIntent);
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_top);
    }

    private void addQuestionItemToMine(int position) {
        QuestionItem tempQuestionItem = questionItems.get(position);
        String addedWord = tempQuestionItem.getWord();
        String addedMeaning = tempQuestionItem.getMeaning();
        String addedPhotoPath = tempQuestionItem.getPhotoPath();
        WordItem addedWordItem = new WordItem(addedWord, addedMeaning, addedPhotoPath);

        MainActivity.wordItems.add(0, addedWordItem);
        Toast.makeText(this, "단어장에 단어가 추가되었습니다.", Toast.LENGTH_SHORT).show();
    }
}
