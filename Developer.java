package com.example.stefano.spinup20;

/**
 * Created by Stefano on 03/04/18.
 */

public class Developer {
    public String user_name;

    public Developer() {
    }

    public Developer(String name) {
        this.user_name = name;
    }

    public String getName() {
        return user_name;
    }

    public void setName(String name) {
        this.user_name = name;
    }
}
