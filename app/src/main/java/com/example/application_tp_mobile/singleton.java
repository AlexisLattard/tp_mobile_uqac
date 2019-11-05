package com.example.application_tp_mobile;

import java.util.ArrayList;

class singleton {
    private static final singleton ourInstance = new singleton();

    static singleton getInstance() {
        return ourInstance;
    }

    private singleton() {
    }
    public ArrayList<String> imagePathList = new ArrayList<String>();
    public int screenWidth;
    public int screenHeight;

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }
}
