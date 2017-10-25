package com.example.q.wordphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Q on 8/26/17.
 */

public class QuestionListViewAdapter extends BaseAdapter{
    Context context;
    int itemLayoutID;
    ArrayList<QuestionItem> questionItems;

    public QuestionListViewAdapter(Context context, int itemLayoutID, ArrayList<QuestionItem> questionItems) {
        this.context = context;
        this.itemLayoutID = itemLayoutID;
        this.questionItems = questionItems;
    }

    @Override
    public int getCount() {
        return questionItems.size();
    }

    @Override
    public Object getItem(int position) {
        return questionItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int index = position;
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(itemLayoutID, parent, false);

        }

        ProgressBar progressBar = convertView.findViewById(R.id.questionItemProgressBar);
        ImageView photoView = convertView.findViewById(R.id.questionItemPhotoView);

        progressBar.setVisibility(View.VISIBLE);
        photoView.setVisibility(View.GONE);

        View[] views = {convertView};

        AsyncTask<View, Object, View> getViewTask = new AsyncTask<View,Object,View>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(View view) {
                super.onPostExecute(view);
            }

            @Override
            protected void onProgressUpdate(Object... values) {
                View convertView = (View) values[0];

                ProgressBar progressBar = convertView.findViewById(R.id.questionItemProgressBar);
                ImageView photoView = convertView.findViewById(R.id.questionItemPhotoView);
                TextView wordView = convertView.findViewById(R.id.questionItemWordView);
                TextView contentView = convertView.findViewById(R.id.questionItemContentView);
                TextView writerView = convertView.findViewById(R.id.questionItemWriterView);
                TextView dateView = convertView.findViewById(R.id.questionItemDateView);
                TextView viewNumView = convertView.findViewById(R.id.questionItemViewNumView);
                TextView replyNumView = convertView.findViewById(R.id.questionItemReplyNumView);

                Bitmap photo = (Bitmap) values[1];
                String word = (String) values[2];
                String content = (String) values[3];
                String writer = (String) values[4];
                String date = (String) values[5];
                String viewNumText = (String) values[6];
                String replyNumText = (String) values[7];

                photoView.setImageBitmap(photo);
                wordView.setText(word);
                contentView.setText(content);
                writerView.setText(writer);
                dateView.setText(date);
                viewNumView.setText(viewNumText);
                replyNumView.setText(replyNumText);
                progressBar.setVisibility(View.GONE);
                photoView.setVisibility(View.VISIBLE);
                super.onProgressUpdate(values);
            }

            @Override
            protected View doInBackground(View... views) {
                View convertView = views[0];

                QuestionItem questionItem = questionItems.get(index);
                String photoPath = questionItem.getPhotoPath();

                Bitmap photo = getBitmap(photoPath);
                String word = questionItem.getWord();
                String content = questionItem.getContent();
                String writer = questionItem.getWriter();
                String date = questionItem.getDate();
                int viewNum = questionItem.getViewNum();
                int replyNum = questionItem.getReplyNum();

                Object[] values = {convertView, photo, word, content, writer, date, Integer.toString(viewNum), Integer.toString(replyNum)};
                publishProgress(values);

                return null;
            }
        };

        getViewTask.execute(views);

        return convertView;
    }

    protected Bitmap getBitmap(String photoPath) {
        Bitmap tempPhoto = null;

        if(photoPath == null)
            return null;

        try {
            Uri imageUri = Uri.parse(photoPath);
            InputStream imageStream = null;
            imageStream = context.getContentResolver().openInputStream(imageUri);
            tempPhoto = BitmapFactory.decodeStream(imageStream);
            tempPhoto = Bitmap.createScaledBitmap(tempPhoto, 50, 50, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tempPhoto;
    }
}

