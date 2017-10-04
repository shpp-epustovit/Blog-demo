package com.epustovit.blogdemo.Model;

/**
 * Created by User on 04.10.2017.
 */

public class Blog {
    public String title;
    public String description;
    public String image;
    public String timestamp;
    public String userId;

    public Blog() {
    }

    public Blog(String title, String description, String image, String timestamp, String userId) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
