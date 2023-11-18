package com.example.votingapp.screens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.votingapp.ApiLib.NetworkRequest;
import com.example.votingapp.MainActivity;
import com.example.votingapp.R;
import com.example.votingapp.constants.Constants;
import com.example.votingapp.fragments.LogsFragment;
import com.example.votingapp.fragments.PasscodeFragment;
import com.example.votingapp.fragments.RoundUpdate;
import com.example.votingapp.fragments.SigninFragment;
import com.example.votingapp.model.Passcode;
import com.example.votingapp.utils.AuthHelper;
import com.example.votingapp.utils.PrefHelper;
import com.example.votingapp.worker.PasscodeValidator;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public class MainScreen extends AppCompatActivity {


    boolean doubleBackToExitPressedOnce = false;

    Toast toast;


    void makeToast(String message){
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(toast != null){
            toast.cancel();
        }
    }



    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            super.onBackPressed();
        }

        this.doubleBackToExitPressedOnce = true;
        makeToast("Please click BACK again to exit");

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    BottomNavigationView bottomNavigationView;

    RoundUpdate roundUpdateFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Intent intent = getIntent();
        String electionCode = intent.getStringExtra(Constants.ELECTION_CODE);
        roundUpdateFragment = com.example.votingapp.fragments.RoundUpdate.newInstance(electionCode, "");
        openFragment(roundUpdateFragment);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(onItemSelectedListener);
        if(!AuthHelper.getInstance(this).isLoggedIn()){
            redirectToSignInScreen();
        }
    }


    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    private void redirectToSignInScreen(){
        AuthHelper.getInstance(MainScreen.this).clear();
        new PrefHelper(MainScreen.this).clearPasscodes();
        Intent myIntent = new Intent(MainScreen.this, LoginSignupScreen.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        MainScreen.this.finish();
        startActivity(myIntent);
    }

    NavigationBarView.OnItemSelectedListener onItemSelectedListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == R.id.re_entry) {
                openFragment(RoundUpdate.newInstance("", ""));
                roundUpdateFragment.clearTextViews();
                return true;
            } else if (item.getItemId() == R.id.refresh) {
                new AlertDialog.Builder(MainScreen.this)
                        .setTitle("Refresh")
                        .setMessage("Do you want to refresh constituency details?")
                        .setIcon(R.drawable.icon)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                openFragment(RoundUpdate.newInstance("", ""));
                                roundUpdateFragment.getCandidates();
                            }})
                        .setNegativeButton("No", null).show();
                return true;
            }else if (item.getItemId() == R.id.logs) {
                openFragment(LogsFragment.newInstance("", ""));
                return true;
            }else {
                new AlertDialog.Builder(MainScreen.this)
                        .setTitle("Logout")
                        .setMessage("Do you want to logout?")
                        .setIcon(R.drawable.icon)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                               redirectToSignInScreen();
                            }})
                        .setNegativeButton("No", null).show();
            }
            return false;
        }
    };


}