package com.example.q.wordphoto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Q on 8/26/17.
 */

public class ReplyListViewAdapter extends BaseAdapter {
    Context context;
    int itemLayoutID;
    ArrayList<ReplyItem> replyItems;

    public ReplyListViewAdapter(Context context, int itemLayoutID, ArrayList<ReplyItem> replyItems) {
        this.context = context;
        this.itemLayoutID = itemLayoutID;
        this.replyItems = replyItems;
    }

    @Override
    public int getCount() {
        return replyItems.size();
    }

    @Override
    public Object getItem(int position) {
        return replyItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(itemLayoutID, parent, false);
        }

        TextView writerView = convertView.findViewById(R.id.replyItemWriterView);
        TextView dateView = convertView.findViewById(R.id.replyItemDateView);
        TextView contentView = convertView.findViewById(R.id.replyItemContentView);
        TextView modifyView = convertView.findViewById(R.id.replyItemModifyView);
        TextView deleteView = convertView.findViewById(R.id.replyItemDeleteView);

        modifyView.setOnClickListener(new KnowIndexOnClickListener(position) {
            int index = position;
            @Override
            public void onClick(View view) {
                ((QuestionDescriptionActivity) context).modifyReply(index);
            }
        });

        deleteView.setOnClickListener(new KnowIndexOnClickListener(position) {
            int index = position;
            @Override
            public void onClick(View view) {
                ((QuestionDescriptionActivity) context).deleteReply(index);
            }
        });

        ReplyItem replyItem = replyItems.get(position);
        String writer = replyItem.getWriter();
        String date = replyItem.getDate();
        String content = replyItem.getContent();

        writerView.setText(writer);
        dateView.setText(date);
        contentView.setText(content);

        return convertView;
    }
}
