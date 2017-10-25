package com.example.q.wordphoto;

import android.content.SharedPreferences;
import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Q on 8/23/17.
 */

public class WordItem {
    String id;
    String word;
    String meaning;
    String musicPath;
    ArrayList<String> photoPaths;
    boolean memorized;
    boolean wordHidden;
    boolean meaningHidden;
    boolean wordChosen;
    boolean meaningChosen;

    public WordItem(String word, String meaning, String photoPath) {
        this.word = word;
        this.id = word + "_" + 0;
        this.meaning = meaning;
        this.photoPaths = new ArrayList<String>();
        this.photoPaths.add(photoPath);
        memorized = false;
    }

    public WordItem(String word, String meaning) {
        this(word, meaning, null);
    }

    public boolean isMemorized() {
        return memorized;
    }

    public void setMemorized(boolean memorized) {
        this.memorized = memorized;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public String getMeaning() {
        return meaning;
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getPhotoPaths() {
        return photoPaths;
    }

    public String getPhotoPath(int index) {
        return photoPaths.get(index);
    }

    public void setPhotoPath(int index, String path) {
        photoPaths.set(index, path);
    }

    public void deletePhotoPath(int index) {
        photoPaths.remove(index);
    }

    public void setMusicPath(String musicPath) {
        this.musicPath = musicPath;
    }

    public String getMusicPath() {
        return musicPath;
    }

    public void hideWord() {
        wordHidden = true;
        wordChosen = true;
        meaningChosen = false;
    }
    public void hideMeaning() {
        meaningHidden = true;
        meaningChosen = true;
        wordChosen = false;
    }
    public void displayWord() {
        wordHidden = false;
        wordChosen = true;
        meaningChosen = false;
    }
    public void displayMeaning() {
        meaningHidden = false;
        meaningChosen = true;
        wordChosen = false;
    }
    public boolean isWordHidden() {
        return wordHidden;
    }
    public boolean isMeaningHidden() {
        return meaningHidden;
    }

    public boolean isWordChosen() {
        return wordChosen;
    }

    public boolean isMeaningChosen() {
        return meaningChosen;
    }

}
