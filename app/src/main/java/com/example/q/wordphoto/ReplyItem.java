package com.example.q.wordphoto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Q on 8/26/17.
 */

public class ReplyItem {
    String writer;
    String content;
    String date;
    SimpleDateFormat format;
    Date currentTime;

    public ReplyItem() {
        this.writer = null;
        this.content = null;

        format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss",Locale.KOREA);
        currentTime = new Date();
        date = format.format(currentTime);
    }

    public ReplyItem(String writer, String content) {
        this();
        this.writer = writer;
        this.content = content;
    }

    public ReplyItem(String writer, String content, String date) {
        this(writer, content);
        this.date = date;
    }

    public void modifyContent(String content) {
        this.content = content;
        this.date = format.format(new Date());
    }
    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getWriter() {
        return writer;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }
}
