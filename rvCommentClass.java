package com.example.stefano.spinup20;

/**
 * Created by Stefano on 03/04/18.
 */

public class rvCommentClass {
    private String creator;
    private String comment;
    private String likes;
    private String button;
    private String appName;

    public rvCommentClass() {
    }

    public rvCommentClass(String creator, String comment, String likes, String button, String appName) {
        this.creator = creator;
        this.comment = comment;
        this.likes = likes;
        this.button = button;
        this.appName = appName;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {

        this.creator = creator;
    }

    public String getComment() {

        return comment;
    }

    public void setComment(String comment) {

        this.comment = comment;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
