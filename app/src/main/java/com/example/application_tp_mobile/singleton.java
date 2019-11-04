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
}
