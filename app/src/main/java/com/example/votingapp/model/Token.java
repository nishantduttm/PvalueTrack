package com.example.votingapp.model;


import com.example.votingapp.ApiLib.NetworkRequest;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;


public class Token implements NetworkRequest.ApiResponse {
    @SerializedName("token")
    private String idToken;

    public Token(String idToken) {
        this.idToken = idToken;
    }

    public String getIdToken() {
        return idToken;
    }

    @Override
    public String string() {
        return idToken;
    }
}
