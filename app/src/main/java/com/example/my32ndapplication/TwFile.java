package com.example.my32ndapplication;

public class TwFile {
    public static final String LOG_TAG = "32XND";
    // Constructor, I hope
    TwFile(String filePath) {
        setFilePath(filePath);
    }
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    private String filePath ;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    @Override
    public  String toString() {
        return filePath;
    }
}
