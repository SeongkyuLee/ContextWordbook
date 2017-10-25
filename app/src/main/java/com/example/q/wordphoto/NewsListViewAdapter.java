package com.example.q.wordphoto;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Q on 9/25/17.
 */

public class NewsListViewAdapter extends BaseAdapter {
    Context context;
    int itemLayoutID;
    ArrayList<News> newsItems;

    public NewsListViewAdapter(Context context, int itemLayoutID, ArrayList<News> newsItems) {
        this.context = context;
        this.itemLayoutID = itemLayoutID;
        this.newsItems = newsItems;
    }

    @Override
    public int getCount() {
        return newsItems.size();
    }

    @Override
    public Object getItem(int position) {
        return newsItems.get(position);
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

        TextView titleView = convertView.findViewById(R.id.newsItemTitleView);
        TextView descriptionView = convertView.findViewById(R.id.newsItemDescriptionView);
        TextView linkView = convertView.findViewById(R.id.newsItemLinkView);

        linkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder callNewsAlertDialogBuilder = new AlertDialog.Builder(context);

                callNewsAlertDialogBuilder
                        .setMessage("원문 기사를 보러 이동하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String url = newsItems.get(position).getLink();
                                        Uri uri = Uri.parse(url);
                                        Intent searchIntent = new Intent(Intent.ACTION_VIEW, uri);
                                        context.startActivity(searchIntent);
                                    }
                                })
                        .setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog callNewsAlertDialog = callNewsAlertDialogBuilder.create();
                callNewsAlertDialog.show();
            }
        });

        News news = newsItems.get(position);
        String title = news.getTitle();
        String description = news.getDescription();

        titleView.setText(title);
        descriptionView.setText(Html.fromHtml(description,Html.FROM_HTML_MODE_COMPACT));

        return convertView;
    }
}
