package com.example.votingapp.utils;




import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.auth0.android.jwt.JWT;
import com.example.votingapp.ApiLib.NetworkRequest;
import com.example.votingapp.constants.Constants;
import com.example.votingapp.model.Token;


public class AuthHelper {

    /**
     * Key for username in the jwt claim
     */
    private static final String JWT_KEY_USERNAME = "username";

    private static final String PREFS = "prefs";
    private static final String PREF_TOKEN = "pref_token";
    private SharedPreferences mPrefs;

    private static AuthHelper sInstance;

    private AuthHelper(@NonNull Context context) {
        mPrefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sInstance = this;
    }

    public static AuthHelper getInstance(@NonNull Context context) {
        if (sInstance == null) {
            sInstance = new AuthHelper(context);
        }
        return sInstance;
    }

    public void setIdToken(@NonNull Token token) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(PREF_TOKEN, token.getIdToken());
        editor.apply();
    }

    @Nullable
    public String getIdToken() {
        return mPrefs.getString(PREF_TOKEN, null);
    }

    public boolean isLoggedIn() {
        String token = getIdToken();
        return token != null;
    }


    public void clear() {
        mPrefs.edit().clear().commit();
    }
}
