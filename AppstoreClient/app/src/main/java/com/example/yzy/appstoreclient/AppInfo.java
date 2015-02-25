package com.example.yzy.appstoreclient;

/**
 * Created by yzy on 15-2-17.
 */
public class AppInfo {
    public static final String NAME = "name";
    public static final String PHOTO = "photo";
    public static final String SUMMARY = "summary";
    public static final String URL = "url";
    private String name;
    private String summary;

    private String photoUrl;
    private String apkUrl;


    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
