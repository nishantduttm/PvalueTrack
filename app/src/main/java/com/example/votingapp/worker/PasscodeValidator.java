package com.example.votingapp.worker;

import static com.example.votingapp.fragments.BaseFragment.log;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.votingapp.ApiLib.NetworkRequest;
import com.example.votingapp.MainActivity;
import com.example.votingapp.constants.Constants;
import com.example.votingapp.fragments.PasscodeFragment;
import com.example.votingapp.fragments.RoundUpdate;
import com.example.votingapp.fragments.SigninFragment;
import com.example.votingapp.model.Passcode;
import com.example.votingapp.model.Token;
import com.example.votingapp.screens.MainScreen;
import com.example.votingapp.utils.AuthHelper;
import com.example.votingapp.utils.PrefHelper;

import java.util.Arrays;

public class PasscodeValidator extends Worker {

    private static final String WORK_RESULT = "work_result";
    public PasscodeValidator(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
    @NonNull
    @Override
    public Result doWork() {
        Data taskData = getInputData();
        String enteredPasscode = taskData.getString(PasscodeFragment.MESSAGE_STATUS);
        enteredPasscode = enteredPasscode != null ? enteredPasscode : "Message has been Sent";
        NetworkRequest networkRequest = NetworkRequest.getInstance();
        AuthHelper authHelper = AuthHelper.getInstance(getApplicationContext());
        Passcode[] passcodes = networkRequest.doGetPasscodesSync(new Token(authHelper.getIdToken()));
        if(passcodes != null) {
            log("info", "doWork: " + Arrays.toString(passcodes));
            for (Passcode passcode : passcodes) {
                if (passcode.getPasscode().equals(enteredPasscode)) {
                    Data outputData = new Data.Builder().putString(WORK_RESULT, "Jobs Finished").build();
                    return Result.success(outputData);
                }
            }
        }
        Data outputData = new Data.Builder().putString(WORK_RESULT, "Jobs Finished").build();
        return Result.failure(outputData);
    }
}