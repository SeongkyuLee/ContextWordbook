package com.example.q.wordphoto;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class CardDescriptionActivity extends WordPhotoParentActivity {
    AnimatorSet mSetRightOut;
    AnimatorSet mSetLeftIn;
    int index;
    boolean mIsBackVisible;
    boolean mIsRunning;
    boolean mIsClicked;
    ArrayList<WordItem> wordItems;
    Handler handler;

    //Android Views
    ProgressBar mProgressBar;
    View mFrontCardView;
    View mBackCardView;
    ImageView mFrontPhotoView;
    ImageView mBackPhotoView;
    TextView mFrontWordView;
    TextView mBackMeaningView;
    Button mPrevBtn;
    Button mNextBtn;

    Thread thread = new Thread() {
        @Override
        public void run() {
            while (mIsRunning) {
                if (mIsClicked) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String photoPath = wordItems.get(index).getPhotoPath(0);
                            if (photoPath == null) {
                                mBackPhotoView.setImageResource(R.color.colorDarkGray);
                            } else {
                                mBackPhotoView.setImageResource(R.color.colorDarkGray);
                                Bitmap photo = getBitmap(photoPath);
                                mBackPhotoView.setImageBitmap(photo);
                            }
                        }
                    });
                    mIsClicked = false;
                }
            };
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_game);
        wordItems = MainActivity.wordItems;
        index = 0;
        handler = new Handler();
        findViews();
        loadAnimations();
        setCards();
        mIsRunning=true;
        mIsClicked=true;
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsRunning = false;
    }

    private void setCards() {
        String word = wordItems.get(index).getWord();
        String meaning = wordItems.get(index).getMeaning();

        mProgressBar.setProgress(index + 1);
        mFrontWordView.setText(word);
        mBackMeaningView.setText(meaning);
    }

    private void findViews() {
        mProgressBar = (ProgressBar) findViewById(R.id.cardGameProgressBar);
        mFrontCardView = findViewById(R.id.cardGameFrontCard);
        mBackCardView = findViewById(R.id.cardGameBackCard);
        mFrontPhotoView = (ImageView) findViewById(R.id.cardGameFrontCardImageView);
        mBackPhotoView = (ImageView) findViewById(R.id.cardGameBackCardImageView);
        mFrontWordView = (TextView) findViewById(R.id.cardGameFrontCardTextView);
        mBackMeaningView = (TextView) findViewById(R.id.cardGameBackCardTextView);
        mPrevBtn = (Button) findViewById(R.id.cardGamePrevBtn);
        mNextBtn = (Button) findViewById(R.id.cardGameNextBtn);

        mProgressBar.setMax(wordItems.size());

        final Animation pressPrevNextBtnAnim = AnimationUtils.loadAnimation(this, R.anim.press_description_word_btn_anim);
        mFrontPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipCard3d();
            }
        });
        mBackPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipCard3d();
            }
        });
        mPrevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPrevCard();
                mPrevBtn.startAnimation(pressPrevNextBtnAnim);
                mIsClicked = true;
            }
        });
        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNextCard();
                mNextBtn.startAnimation(pressPrevNextBtnAnim);
                mIsClicked = true;
            }
        });
    }

    private void showPrevCard() {
        if (index == 0) {
            Toast.makeText(this, "처음 단어입니다.", Toast.LENGTH_SHORT).show();
        } else {
            index--;
            if(mIsBackVisible) {
                flipCard2d();
            }
            setCards();
        }
    }

    private void showNextCard() {
        if (index == wordItems.size() - 1) {
            Toast.makeText(this, "마지막 단어입니다", Toast.LENGTH_SHORT).show();
        } else {
            index++;
            if(mIsBackVisible) {
                flipCard2d();
            }
            setCards();
        }
    }

    private void loadAnimations() {
        changeCameraDistance();
        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.out_animation);
        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.in_animation);
    }

    private void changeCameraDistance() {
        float scale = 8000 * getResources().getDisplayMetrics().density;
        mFrontCardView.setCameraDistance(scale);
        mBackCardView.setCameraDistance(scale);
    }

    public void flipCard3d() {
        if(!mIsBackVisible) {
            mSetRightOut.setTarget(mFrontCardView);
            mSetLeftIn.setTarget(mBackCardView);
            mSetRightOut.start();
            mSetLeftIn.start();
            mIsBackVisible = true;
        } else {
            mSetRightOut.setTarget(mBackCardView);
            mSetLeftIn.setTarget(mFrontCardView);
            mSetRightOut.start();
            mSetLeftIn.start();
            mIsBackVisible = false;
        }
        //give time to load card photo
    }

    public void flipCard2d() {
        if(!mIsBackVisible) {
            mFrontCardView.setAlpha(0);
            mFrontCardView.setRotationY(180);
            mBackCardView.setRotationY(0);
            mBackCardView.setAlpha(1);
            mIsBackVisible = true;
        } else {
            mBackCardView.setAlpha(0);
            mBackCardView.setRotationY(180);
            mFrontCardView.setRotationY(0);
            mFrontCardView.setAlpha(1);
            mIsBackVisible = false;
        }
    }

    private Bitmap getBitmap(String photoPath) {
        Bitmap tempPhoto = null;

        if(photoPath == null)
            return null;

        try {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    0);
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
