package com.example.votingapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.votingapp.R;
import com.example.votingapp.screens.LoginSignupScreen;
import com.example.votingapp.utils.AuthHelper;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;

public class BaseFragment extends Fragment {

    Toast toast;
    void openFragment(Fragment fragment){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.loginSignUpContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    void openFragmentInMainScreen(Fragment fragment){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    void makeToast(String message){
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(this.getContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(toast != null){
            toast.cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(appUpdater != null) {
            appUpdater.stop();
        }
    }

    private  AppUpdater appUpdater;

    private static final String BACK_STACK_ROOT_TAG = "root_fragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  super.onCreateView(inflater, container, savedInstanceState);
        appUpdater = new AppUpdater(this.getContext())
                .setUpdateFrom(UpdateFrom.GITHUB)
                .setGitHubUserAndRepo("nishantduttm", "PvalueTrack")
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateXML("https://raw.githubusercontent.com/nishantduttm/PvalueTrack/main/app/update-changelog.json")
                .setDisplay(Display.DIALOG)
                .showAppUpdated(true);
        appUpdater.start();
        if(!AuthHelper.getInstance(this.getContext()).isLoggedIn()){
            openLoginActivity();
        }
        return v;
    }

    void openLoginActivity(){
        Activity currentActivity = getActivity();
        AuthHelper.getInstance(currentActivity).clear();
        Intent myIntent = new Intent(currentActivity, LoginSignupScreen.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        currentActivity.finish();
        startActivity(myIntent);
    }

}
