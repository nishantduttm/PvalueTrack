package com.example.votingapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.votingapp.model.Candidate;
import com.example.votingapp.model.LogEntry;
import com.example.votingapp.model.Passcode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class PrefHelper {

    private static final String PREF_FILE_NAME = "VotingAppPref"; // Name of your preference file
    private static final String KEY_USER_NAME = "user_name"; // Example keys
    private static final String KEY_PASSWORD = "password"; // Example keys

    private static final String AC_CODE = "ac_code";

    private static final String AC_NAME = "ac_name";

    private static final String PROJECT_NAME = "project_name";

    private static final String CANDIDATES = "candidates";

    private static final String PASSCODE = "passcode";
    private static final String LOGS = "logs";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    // Constructor
    public PrefHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Methods for storing and retrieving data

    public void saveCredentials(String username, String password){
        editor.putString(KEY_USER_NAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    public String getUserName(){
        return sharedPreferences.getString(KEY_USER_NAME, "");
    }

    public String getPassword(){
        return sharedPreferences.getString(KEY_PASSWORD, "");
    }



    public void saveACDetails(String ac_code, String ac_name){
        editor.putString(AC_CODE, ac_code);
        editor.putString(AC_NAME, ac_name);
        editor.apply();
    }

    public void savePasscode(Passcode passcode){
        String data = new Gson().toJson(passcode);
        editor.putString(PASSCODE, data);
        editor.apply();
    }

    public Passcode getPasscode(){
        String data = sharedPreferences.getString(PASSCODE, "");
        Log.d("info", "getSavedCandidateData: "+data);
        if(!data.equals("")){
            return new Gson().fromJson(data, Passcode.class);
        }
        return new Passcode();
    }

    public void clearPasscodes(){
        editor.remove(PASSCODE);
        editor.apply();
    }

    public void saveCandidatesData(Candidate[] candidates){
        String data = new Gson().toJson(candidates);
        editor.putString(CANDIDATES, data);
        editor.apply();
    }

    public void saveLogs(LogEntry[] logEntries){
        String data = new Gson().toJson(logEntries);
        editor.putString(LOGS, data);
        editor.apply();
    }

    public void saveLog(LogEntry logEntry){
        LogEntry[] savedLogs = getLogs();
        LogEntry[] newSavedLogs = new LogEntry[savedLogs.length + 1];
        newSavedLogs[0] = logEntry;
        for(int i = 1; i < newSavedLogs.length; i++){
            newSavedLogs[i] = savedLogs[i - 1];
        }
        String data = new Gson().toJson(newSavedLogs);
        editor.putString(LOGS, data);
        editor.apply();
    }

    public LogEntry[] getLogs(){
        String data = sharedPreferences.getString(LOGS, "");
        if(!data.equals("")){
            return new Gson().fromJson(data, LogEntry[].class);
        }
        return new LogEntry[0];
    }

    public Candidate[] getSavedCandidateData(){
        String data = sharedPreferences.getString(CANDIDATES, "");
        Log.d("info", "getSavedCandidateData: "+data);
        if(!data.equals("")){
            return new Gson().fromJson(data, Candidate[].class);
        }
        return new Candidate[0];
    }

    public void saveProject(String project){
        editor.putString(PROJECT_NAME, project);
        editor.apply();
    }

    public void clearProjectData(){
        editor.remove(PROJECT_NAME);
        editor.apply();
    }

    public void clearACData(){
        editor.remove(AC_CODE);
        editor.remove(AC_NAME);
        editor.apply();
    }

    public String getProject(){
        return sharedPreferences.getString(PROJECT_NAME, "");
    }

    public String getACCode(){
        return sharedPreferences.getString(AC_CODE, "");
    }

    public String getACName(){
        return sharedPreferences.getString(AC_NAME, "");
    }

    public void clearPreferences() {
        editor.clear();
        editor.apply();
    }
}
