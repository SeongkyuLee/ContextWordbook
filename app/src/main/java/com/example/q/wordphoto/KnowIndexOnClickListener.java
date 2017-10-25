package com.example.q.wordphoto;

import android.view.View;

public abstract class KnowIndexOnClickListener implements View.OnClickListener {

    int index;

    public KnowIndexOnClickListener(int index) {
        this.index = index;
    }
}
