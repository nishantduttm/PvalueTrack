package com.example.votingapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.example.votingapp.screens.MainScreen;
import com.example.votingapp.utils.AuthHelper;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

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
        if(isAdded()) {
            toast = Toast.makeText(this.getContext(), message, Toast.LENGTH_SHORT);
            toast.show();
        }
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
    }

    private  AppUpdater appUpdater;

    private static final String BACK_STACK_ROOT_TAG = "root_fragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  super.onCreateView(inflater, container, savedInstanceState);
        if(!AuthHelper.getInstance(this.getContext()).isLoggedIn()){
            openLoginActivity();
        }
        new AppUpdaterUtils(getActivity())
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON("https://raw.githubusercontent.com/nishantduttm/PvalueTrack/main/app/update-changelog.json")
                .withListener(new AppUpdaterUtils.UpdateListener() {
                    @Override
                    public void onSuccess(Update update, Boolean isUpdateAvailable) {
                        Log.d("Latest Version", update.getLatestVersion());
                        Log.d("Latest Version Code", update.getLatestVersionCode().toString());
                        Log.d("Release notes", update.getReleaseNotes());
                        Log.d("URL", update.getUrlToDownload().toString());
                        Log.d("Is update available?", Boolean.toString(isUpdateAvailable));
                        if(isUpdateAvailable){
                            new AlertDialog.Builder(BaseFragment.this.getActivity())
                                    .setTitle("Update Available")
                                    .setMessage("Do you want update app?")
                                    .setIcon(R.drawable.icon)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(update.getUrlToDownload().toString()));
                                            startActivity(browserIntent);
                                        }})
                                    .setNegativeButton("No", null).show();
                        }
                    }

                    @Override
                    public void onFailed(AppUpdaterError error) {
                        Log.d("AppUpdater Error", "Something went wrong");
                    }
                }).start();
        return v;
    }

    void openLoginActivity(){
        Activity currentActivity = getActivity();
        AuthHelper.getInstance(currentActivity).clear();
        Intent myIntent = new Intent(currentActivity, LoginSignupScreen.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(myIntent);
        currentActivity.finish();
    }

    public static void log(String tag, String message){
        Log.d(tag, message);
        FirebaseCrashlytics firebaseCrashlytics  = FirebaseCrashlytics.getInstance();
        String[] messageparts = message.split(" ", 2);
        firebaseCrashlytics.setCustomKey(messageparts[0], messageparts[1]);
    }

}
