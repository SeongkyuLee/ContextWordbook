package com.example.q.wordphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Q on 8/23/17.
 */

public class WordListViewAdapter extends BaseAdapter {
    Context context;
    int itemLayoutID;
    ArrayList<WordItem> wordItems;

    public WordListViewAdapter(Context context, int itemLayoutID, ArrayList<WordItem> wordItems) {
        this.context = context;
        this.itemLayoutID = itemLayoutID;
        this.wordItems = wordItems;
    }

    @Override
    public int getCount() {
        return wordItems.size();
    }

    @Override
    public Object getItem(int position) {
        return wordItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(itemLayoutID, parent, false);
        }

        TextView wordView = convertView.findViewById(R.id.wordView);
        TextView meaningView = convertView.findViewById(R.id.meaningView);

        WordItem wordItem = wordItems.get(position);
        String word = wordItem.getWord();
        String meaning = wordItem.getMeaning();

        wordView.setText(word);
        meaningView.setText(meaning);

        if((wordView.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) == Paint.STRIKE_THRU_TEXT_FLAG) {
            wordView.setPaintFlags(wordView.getPaintFlags() ^ Paint.STRIKE_THRU_TEXT_FLAG);
            meaningView.setPaintFlags(meaningView.getPaintFlags() ^ Paint.STRIKE_THRU_TEXT_FLAG);
        }

        if(wordItem.isMemorized()) {
            wordView.setPaintFlags(wordView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            meaningView.setPaintFlags(meaningView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        if(wordItem.isWordChosen() && wordItem.isWordHidden()) {
            if(wordView.getVisibility() == View.VISIBLE) {
                Animation hideAnimation = AnimationUtils.loadAnimation(context, R.anim.hide_word_anim);
                wordView.setAnimation(hideAnimation);
                wordView.setVisibility(View.INVISIBLE);
            }
        } else if (wordItem.isWordChosen() && !wordItem.isWordHidden()){
            if(wordView.getVisibility() == View.INVISIBLE) {
                Animation hideAnimation = AnimationUtils.loadAnimation(context, R.anim.show_word_anim);
                wordView.setAnimation(hideAnimation);
                wordView.setVisibility(View.VISIBLE);
            }
        }


        if(wordItem.isMeaningChosen() && wordItem.isMeaningHidden()) {
            if(meaningView.getVisibility() == View.VISIBLE) {
                Animation hideAnimation = AnimationUtils.loadAnimation(context, R.anim.hide_word_anim);
                meaningView.setAnimation(hideAnimation);
                meaningView.setVisibility(View.INVISIBLE);
            }
        } else if (wordItem.isMeaningChosen() && !wordItem.isMeaningHidden()) {
            if(meaningView.getVisibility() == View.INVISIBLE) {
                Animation hideAnimation = AnimationUtils.loadAnimation(context, R.anim.show_word_anim);
                meaningView.setAnimation(hideAnimation);
                meaningView.setVisibility(View.VISIBLE);
            }
        }



        return convertView;
    }
}
