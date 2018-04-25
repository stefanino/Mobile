package com.example.stefano.spinup20;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Stefano on 10/04/18.
 */

public class rvCreatedApp implements java.io.Serializable{
    private String appName;
    private String appCategory;

    public rvCreatedApp() {
            }

    public rvCreatedApp(String appName, String appCategory) {

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
