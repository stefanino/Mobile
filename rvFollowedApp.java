package com.example.stefano.spinup20;

/**
 * Created by Stefano on 07/04/18.
 */

public class rvFollowedApp implements java.io.Serializable{
    private String appName;

    public rvFollowedApp() {
    }

    public rvFollowedApp(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
