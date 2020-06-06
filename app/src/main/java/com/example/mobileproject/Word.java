package com.example.mobileproject;

import java.io.Serializable;
import java.util.ArrayList;

public class Word implements Serializable {
    private String korean, english;

    public Word(String korean, String english) {
        this.korean = korean;
        this.english = english;
    }

    public String getKorean() {
        return korean;
    }

    public String getEnglish() {
        return english;
    }
}
