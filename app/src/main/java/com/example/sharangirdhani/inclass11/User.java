package com.example.sharangirdhani.inclass11;

import java.io.Serializable;

/**
 * Created by sharangirdhani on 11/20/17.
 */

public class User implements Serializable {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String img;
    String user_id;

    public User() {}

    public User(String firstName, String lastName, String email, String password, String img, String user_id) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.img = img;
        this.user_id = user_id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
