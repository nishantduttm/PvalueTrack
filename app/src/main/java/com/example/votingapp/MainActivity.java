package com.example.votingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;

import com.example.votingapp.screens.LoginSignupScreen;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;


public class MainActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
       finish();
       super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build());
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this, LoginSignupScreen.class);
        MainActivity.this.finish();
        startActivity(intent);
    }
}