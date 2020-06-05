package com.example.mobileproject.ui;

import java.io.Serializable;
import java.util.ArrayList;

public class Word implements Serializable {
    private String korean, english;
    private ArrayList<String> koreanList;
    private ArrayList<String> englishList;

    public Word(String korean, String english) {
        this.korean = korean;
        this.english = english;
    }

    public void setKorean(String korean) {
        this.korean = korean;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getKorean() {
        return korean;
    }

    public String getEnglish() {
        return english;
    }

    public void addWord(String english_word, String korean_word) {
        englishList.add(english_word);
        koreanList.add(korean_word);
    }
}
