package com.example.votingapp.model;

import com.google.gson.annotations.SerializedName;

public class User {
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @SerializedName("Email")
    private String userName;

    @SerializedName("Password")
    private String password;

}
