package com.example.q.wordphoto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Q on 9/6/17.
 */

public class StopWatchListViewAdapter extends BaseAdapter{
    Context context;
    int itemLayoutID;
    ArrayList<String[]> times;

    public StopWatchListViewAdapter(Context context, int itemLayoutID, ArrayList<String[]> times) {
        this.context = context;
        this.itemLayoutID = itemLayoutID;
        this.times = times;
    }

    @Override
    public int getCount() {
        return times.size();
    }

    @Override
    public Object getItem(int position) {
        return times.get(position);
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

        TextView indexView = convertView.findViewById(R.id.stopWatchItemIndexView);
        TextView elapsedTimeView = convertView.findViewById(R.id.stopWatchItemElapsedTimeView);
        TextView elapsedLapTimeView = convertView.findViewById(R.id.stopWatchItemElapsedLapTimeView);

        indexView.setText(Integer.toString(times.size() - position));
        elapsedTimeView.setText(times.get(position)[0]);
        elapsedLapTimeView.setText(times.get(position)[1]);

        return convertView;
    }
}
