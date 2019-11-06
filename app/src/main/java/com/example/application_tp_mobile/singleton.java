package com.example.application_tp_mobile;

import java.util.ArrayList;

class Singleton {
    private static final Singleton ourInstance = new Singleton();
    public ArrayList<String> imagePathList = new ArrayList<>();

    private Singleton() {
    }

    static Singleton getInstance() {
        return ourInstance;
    }
}
