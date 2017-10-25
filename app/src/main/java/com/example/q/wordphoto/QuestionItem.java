package com.example.q.wordphoto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Q on 8/26/17.
 */

public class QuestionItem {
    String id;
    String word;
    String meaning;
    String writer;
    String content;
    String photoPath;
    ArrayList<ReplyItem> replies;

    int viewNum;

    SimpleDateFormat format;
    Date currentTime;
    String date;

    public QuestionItem(String word, String meaning, String content, String writer, String photoPath) {
        this.word = word;
        this.meaning = meaning;
        this.writer = writer;
        this.content = content;
        this.photoPath = photoPath;

        this.viewNum = 0;
        format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA);
        currentTime = new Date();
        date = format.format(currentTime);

        this.id = word + "_" + 0;
    }

    public QuestionItem(String word, String meaning, String content, String writer) {
        this(word, meaning, content, writer, null);
    }

    public void increaseView() {
        viewNum++;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public void setReply(ArrayList<ReplyItem> replyItems) {
        this.replies = replyItems;

    }
    public void setId(String id) {
        this.id = id;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setViewNum(int viewNum) {
        this.viewNum = viewNum;
    }
    public void addReply() {
        ReplyItem tempReply = new ReplyItem();
        replies.add(tempReply);
    }

    public void deleteReply(int position) {
        replies.remove(position);
    }

    public String getWord() {
        return word;
    }

    public ArrayList<ReplyItem> getReplies() {
        return replies;
    }

    public int getReplyNum() {
        return replies.size();
    }

    public int getViewNum() {
        return viewNum;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public String getWriter() {
        return writer;
    }

    public String getMeaning() {
        return meaning;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public String getId() {
        return id;
    }
}
