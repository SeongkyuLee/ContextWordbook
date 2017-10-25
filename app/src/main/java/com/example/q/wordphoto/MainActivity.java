package com.example.q.wordphoto;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends WordPhotoParentActivity {
    public static final String WORDS_PREFS_NAME = "WordItems";
    public static final String MEMBERS_PREFS_NAME = "Members";
    SharedPreferences wordPreferences;
    SharedPreferences memberPreferences;

    public static String email;
    public static String id;
    public static ArrayList<WordItem> wordItems = new ArrayList<WordItem>();

    public static int visitNumber = 0;
    public static boolean logged = false;
    public static boolean autoLogged = false;
    public static boolean tested = true;

    ListView wordListView;
    WordListViewAdapter wordListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setListView();
        setup();
        if(visitNumber == 0) {
            setMemberPreferences();
            setWordItemsFromPreferences();
            Toast.makeText(MainActivity.this,"오늘도 단어를 외워봅시다.",Toast.LENGTH_SHORT).show();
            visitNumber++;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        wordListViewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveWordItemsAtPreferences();
        saveMemberPreferences();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setup() {
        wordPreferences = getSharedPreferences(WORDS_PREFS_NAME, MODE_PRIVATE);
        memberPreferences = getSharedPreferences(MEMBERS_PREFS_NAME, MODE_PRIVATE);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(myToolbar);

        final Button addWordBtn = (Button) findViewById(R.id.mainAddWordBtn);
        addWordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callAddWord();
                Animation pressBtnAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.press_btn_anim);
                addWordBtn.startAnimation(pressBtnAnimation);
            }
        });
    }
    private void setMemberPreferences() {
        boolean autoLogged = memberPreferences.getBoolean("autoLogged",false);
        if(autoLogged) {
            String email = memberPreferences.getString("email",null);
            String id = memberPreferences.getString("id",null);
            boolean logged = memberPreferences.getBoolean("logged",false);

            this.email = email;
            this.id = id;
            this.logged = logged;
            this.autoLogged = autoLogged;
        }
    }

    private void saveMemberPreferences() {
        SharedPreferences.Editor editor = memberPreferences.edit();
        editor.putString("email", this.email);
        editor.putString("id", this.id);
        editor.putBoolean("logged",this.logged);
        editor.putBoolean("autoLogged",this.autoLogged);
        editor.apply();
    }

    private void setWordItemsFromPreferences() {
        int length = wordPreferences.getInt("length",0);
        for(int i = 0; i < length; i++) {
            String id = wordPreferences.getString(Integer.toString(i),null);
            String word = wordPreferences.getString(id+"_word", null);
            String meaning = wordPreferences.getString(id+"_meaning", null);
            String photoPath = wordPreferences.getString(id+"_photoPath", null);
            String musicPath = wordPreferences.getString(id+"_musicPath", null);
            Boolean memorized = wordPreferences.getBoolean(id+"_memorized", false);
            WordItem tempWordItem = new WordItem(word, meaning, photoPath);
            tempWordItem.setMusicPath(musicPath);
            tempWordItem.setMemorized(memorized);
            tempWordItem.setId(id);

            if(wordItems.size() <= i) {
                wordItems.add(tempWordItem);
            } else {
                wordItems.set(i, tempWordItem);
            }
        }
    }

    private void saveWordItemsAtPreferences() {
        int length = wordItems.size();
        SharedPreferences.Editor editor = wordPreferences.edit();

        editor.putInt("length", length);

        for(int i = 0; i < length; i++) {
            String id = wordItems.get(i).getId();
            String word = wordItems.get(i).getWord();
            String meaning = wordItems.get(i).getMeaning();
            String photoPath = wordItems.get(i).getPhotoPath(0);
            String musicPath = wordItems.get(i).getMusicPath();
            Boolean memorized = wordItems.get(i).isMemorized();

            int idIndex = 0;
            //기존에 등록된 키인지 확인하고 본인에게 맞는 키까지 찾아줌
            while(wordPreferences.contains(id+"_word")) {
                idIndex = Integer.parseInt(id.split("_")[1]);
                idIndex++;

                id = word + "_" + idIndex;
                Log.d("id", i + "번째 idIndex : " + idIndex);
            }
            editor.putString(Integer.toString(i), id);
            editor.putString(id + "_word", word);
            editor.putString(id + "_meaning", meaning);
            editor.putString(id + "_photoPath", photoPath);
            editor.putString(id + "_musicPath", musicPath);
            editor.putBoolean(id + "_memorized", memorized);
            Log.d("id", i + "번째 id : " + id);
            editor.apply();
        }

        Log.d("shared preference", String.valueOf(wordPreferences.getAll()));
    }

    private void setListView() {
        wordListView = (ListView) findViewById(R.id.wordListView);
        wordListViewAdapter = new WordListViewAdapter(MainActivity.this, R.layout.listview_word_item, wordItems);
        wordListView.setAdapter(wordListViewAdapter);
        wordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                callWordDescription(position);
            }
        });
        wordListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final int index = position;
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(view.getContext());

                String[] items = {"뉴스 보기", "외움", "수정", "삭제"};
                if(wordItems.get(position).isMemorized()) {
                    items[1] = "까먹음";
                } else {
                    items[1] = "외움";
                }

                alertBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0) {
                            callReadNews(index);
                        } else if(i == 1) {
                            if(wordItems.get(position).isMemorized()) {
                                unmemorizeWordItem(position);
                            } else {
                                memorizeWordItem(position);
                            }
                        } else if(i == 2) {
                            callModifyWord(position);
                        } else if(i == 3) {
                            deleteWordItem(index);
                        }
                    }
                });

                /*
                alertBuilder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteWordItem(index);
                    }
                });

                if (wordItems.get(position).isMemorized()) {
                    alertBuilder.setMessage("암기 표시를 없애고 싶으면 까먹음 버튼, 단어장에서 삭제를 원하면 삭제 버튼을 눌러주세요.");
                    alertBuilder.setNegativeButton("까먹음", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            unmemorizeWordItem(index);
                        }
                    });
                } else {
                    alertBuilder.setMessage("암기 표시를 하고 싶으면 외움 버튼, 단어장에서 삭제를 원하면 삭제 버튼을 눌러주세요.");
                    alertBuilder.setNegativeButton("외움", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            memorizeWordItem(index);
                        }
                    });
                }

                alertBuilder.setNeutralButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callModifyWord(index);
                    }
                });
                */
                alertBuilder.show();

                return true;
            }
        });
    }

    public void callReadNews(int index) {
        Intent intent = new Intent(MainActivity.this, NewsActivity.class);
        intent.putExtra("index", index);
        startActivity(intent);
    }

    public void callAddWord() {
        Button addWordBtn = (Button) findViewById(R.id.mainAddWordBtn);
        Intent intent = new Intent(this, AddWordActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_top);
    }

    public void callWordDescription(int index) {
        Intent wordDescriptionIntent = new Intent(this, WordDescriptionActivity.class);
        wordDescriptionIntent.putExtra("index",index);
        startActivity(wordDescriptionIntent);
    }
    public void callModifyWord(int index) {
        Intent modifyWordIntent = new Intent(this, ModifyWordActivity.class);
        modifyWordIntent.putExtra("index",index);
        startActivity(modifyWordIntent);
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_top);
    }

    public void deleteWordItem(int position) {
        wordListViewAdapter.notifyDataSetChanged();
        wordItems.remove(position);
    }

    public void memorizeWordItem(int position) {
        WordItem tempWordItem = wordItems.get(position);
        tempWordItem.setMemorized(true);

        wordItems.remove(position);
        wordItems.add(tempWordItem);
        wordListViewAdapter.notifyDataSetChanged();
    }

    public void unmemorizeWordItem(int position) {
        WordItem tempWordItem = wordItems.get(position);
        tempWordItem.setMemorized(false);

        wordItems.remove(position);
        wordItems.add(0, tempWordItem);
        wordListViewAdapter.notifyDataSetChanged();
    }

    public void wordReset() {
        wordItems.clear();
        wordListViewAdapter.notifyDataSetChanged();
        Toast.makeText(this, "단어장을 지웠습니다.", Toast.LENGTH_SHORT).show();
    }

    public void callSendBug() {
        Intent sendBugIntent = new Intent(MainActivity.this, SendBugActivity.class);
        startActivity(sendBugIntent);
    }
    public void callAskQuestion() {
        if(logged) {
            Intent askQuestionIntent = new Intent(MainActivity.this, QuestionActivity.class);
            startActivity(askQuestionIntent);
        } else {
            callLoginDialog();
        }
    }
    public void callLoginDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        alertDialogBuilder.setTitle("로그인");

        alertDialogBuilder
                .setMessage("단어 질문을 하기 위해서 로그인 하셔야 합니다.")
                .setCancelable(false)
                .setPositiveButton("로그인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 로그인 및 회원가입 페이지로 간다
                                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(loginIntent);
                            }
                        })
                .setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog loginDialog = alertDialogBuilder.create();

        loginDialog.show();
    }

    public void callStopWatch() {
        Intent stopWatchIntent = new Intent(this, StopWatchActivity.class);
        startActivity(stopWatchIntent);
    }

    public void callCardDescription() {
        Intent cardDescriptionIntent = new Intent(this, CardDescriptionActivity.class);
        startActivity(cardDescriptionIntent);
    }

    // Toolbar에 메뉴들을 추가해주기 위해서 오버라이딩 해줌
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem loginItem = menu.findItem(R.id.mainMenuLogin);
        if(logged) {
            loginItem.setTitle("로그아웃");
        } else {
            loginItem.setTitle("로그인");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mainMenuHideWord:
                if(item.isChecked()) {
                    for(int i = 0; i < wordItems.size();i++) {
                        wordItems.get(i).displayWord();
                        wordListViewAdapter.notifyDataSetChanged();
                    }
                    item.setChecked(false);
                    item.setIcon(R.drawable.ic_visibility_off_white_24dp);
                } else {
                    for(int i = 0; i < wordItems.size();i++) {
                        wordItems.get(i).hideWord();
                        wordListViewAdapter.notifyDataSetChanged();
                    }
                    item.setChecked(true);
                    item.setIcon(R.drawable.ic_visibility_white_24dp);
                }

                return true;

            case R.id.mainMenuHideMeaning:
                if(item.isChecked()) {
                    for(int i = 0; i < wordItems.size();i++) {
                        wordItems.get(i).displayMeaning();
                        wordListViewAdapter.notifyDataSetChanged();
                    }
                    item.setChecked(false);
                    item.setIcon(R.drawable.ic_speaker_notes_off_white_24dp);
                } else {
                    for(int i = 0; i < wordItems.size();i++) {
                        wordItems.get(i).hideMeaning();
                        wordListViewAdapter.notifyDataSetChanged();
                    }
                    item.setChecked(true);
                    item.setIcon(R.drawable.ic_speaker_notes_white_24dp);
                }

                return true;

            case R.id.mainMenuAskQuestion:
                callAskQuestion();
                return true;

            case R.id.mainMenuSendBug:
                callSendBug();
                return true;

            case R.id.mainMenuStopWatch:
                callStopWatch();
                return true;

            case R.id.mainMenuLogin:
                if(logged) {
                    logged = false;
                    autoLogged = false;
                    Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    callLoginDialog();
                }
                return true;

            case R.id.mainMenuCardGame:
                if(wordItems.size() > 0) {
                    callCardDescription();
                } else {
                    Toast.makeText(this, "최소 한 개 이상의 단어를 등록해주세요.", Toast.LENGTH_SHORT).show();
                }

                return true;

            case R.id.mainMenuReset:
                AlertDialog.Builder resetAlertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                resetAlertDialogBuilder
                        .setMessage("작성한 단어와 의미들을 모두 삭제 하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("삭제",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        wordReset();
                                    }
                                })
                        .setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog resetDialog = resetAlertDialogBuilder.create();

                resetDialog.show();

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


}
