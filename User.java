package com.example.stefano.spinup20;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefano on 15/03/18.
 */

public class User {

    private String name;
    private List<App> created_apps = new ArrayList<>();
    private List<App> followed_apps = new ArrayList<>();

    public User() {
    }

    public User(String n, App created_app, App followed_app) {
        this.name = n;
        this.created_apps.add(created_app);
        this.followed_apps.add(followed_app);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
