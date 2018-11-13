package com.example.uyen.demoloadmusic;

public class Song {
    private String mName;
    private int mFile;

    public Song(String name, int file) {
        mName = name;
        mFile = file;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getFile() {
        return mFile;
    }

    public void setFile(int file) {
        mFile = file;
    }
}
