package com.example.votingapp.utils;

import android.content.Context;
import android.util.Log;

import com.example.votingapp.constants.Constants;
import com.example.votingapp.model.Candidate;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileHelper {

    Context context;

    static FileHelper instance;

    public FileHelper(Context context){
        this.context = context;
    }


    public void saveCandidates(Candidate[] candidates){
        String data = new Gson().toJson(candidates);
        writeToFile(data, this.context, Constants.CANDIDATE_DATA_FILE_NAME);
    }

    public Candidate[] getCandidates(){
        try{
            String data = readFromFile(this.context, Constants.CANDIDATE_DATA_FILE_NAME);
            Log.d("info", "getCandidates: "+data);
            Candidate[]  candidates = new Gson().fromJson(data, Candidate[].class);
            return candidates;
        }catch (JsonSyntaxException jsonSyntaxException){
            return new Candidate[0];
        }catch (Exception e){
            return new Candidate[0];
        }
    }

    private void writeToFile(String data,Context context, String fileName) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(Context context, String fileName) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(fileName);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
}
