package com.example.stefano.spinup20;

/**
 * Created by Stefano on 04/04/18.
 */

public class rvAppClass implements java.io.Serializable{
    private String appName;
    private String appCategory;

    public rvAppClass() {
    }

    public rvAppClass(String appName, String appCategory) {
        this.appName = appName;
        this.appCategory = appCategory;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppCategory() {
        return appCategory;
    }

    public void setAppCategory(String appCategory) {
        this.appCategory = appCategory;
    }
}
