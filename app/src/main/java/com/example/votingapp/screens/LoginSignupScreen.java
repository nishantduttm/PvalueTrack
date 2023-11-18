package com.example.votingapp.screens;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.votingapp.ApiLib.NetworkRequest;
import com.example.votingapp.R;
import com.example.votingapp.constants.Constants;
import com.example.votingapp.fragments.PasscodeFragment;
import com.example.votingapp.fragments.SigninFragment;
import com.example.votingapp.model.Token;
import com.example.votingapp.model.User;
import com.example.votingapp.utils.AuthHelper;
import com.example.votingapp.utils.PrefHelper;


public class LoginSignupScreen extends AppCompatActivity {


    AuthHelper mAuthHelper;

    PrefHelper prefHelper;
    Toast toast;


    boolean doubleBackToExitPressedOnce = false;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup_screen);

        prefHelper = new PrefHelper(this);

        mAuthHelper = AuthHelper.getInstance(this);

        if (mAuthHelper.isLoggedIn()) {
            openFragment(PasscodeFragment.newInstance("", ""));
        }else{
            openFragment(SigninFragment.newInstance("", ""));
        }
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.loginSignUpContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}