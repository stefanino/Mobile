package com.example.stefano.spinup20;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefano on 26/03/18.
 */

public class App {
    private String app_name;
    private String category;
    private String follower;
    private double rate;
    private List<User> developers = new ArrayList<>();

    public App() {
    }

    public App(String name, String category, String follower) {
        this.app_name = name;
        this.category = category;
        this.rate = 0.0;
    }

    public String getApp_name() {
        return app_name;
    }

    public String getCategory() {
        return category;
    }

    public void setApp_name(String name) {
        this.app_name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }
}
