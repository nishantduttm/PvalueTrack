package com.example.votingapp.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.votingapp.ApiLib.NetworkRequest;
import com.example.votingapp.MainActivity;
import com.example.votingapp.R;
import com.example.votingapp.constants.Constants;
import com.example.votingapp.model.LastRoundData;
import com.example.votingapp.model.Passcode;
import com.example.votingapp.model.Token;
import com.example.votingapp.screens.LoginSignupScreen;
import com.example.votingapp.screens.MainScreen;
import com.example.votingapp.utils.AuthHelper;
import com.example.votingapp.utils.PrefHelper;
import com.example.votingapp.worker.PasscodeValidator;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import lombok.val;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PasscodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PasscodeFragment extends BaseFragment {

    public static final String MESSAGE_STATUS = "PASSCODE";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText[] passcodeEditTexts;

    Button submitButton;
    String enteredPasscode;

    ProgressDialog mProgressDialog;

    PrefHelper prefHelper;

    Token token ;

    Activity mainScreenActivity;

    AuthHelper authHelper;

    static boolean isNewPasscodeRequired;

    private static final  char BIGGER_DOT = '\u2B24';

    public PasscodeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param isNewPassCodeRequired Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PasscodeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PasscodeFragment newInstance(String isNewPassCodeRequired, String param2) {
        PasscodeFragment fragment = new PasscodeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, isNewPassCodeRequired);
        args.putString(ARG_PARAM2, param2);
        isNewPasscodeRequired = isNewPassCodeRequired.equals("true");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    void setOnKeyListeners(){
        for(int i = 0; i < passcodeEditTexts.length - 1; i++){
            final EditText editText = passcodeEditTexts[i];
            final EditText nextEditText = passcodeEditTexts[i + 1];
            final EditText previousEditText;
            if(i > 0) {
                 previousEditText = passcodeEditTexts[i - 1];
            }else{
                previousEditText = null;
            }
            passcodeEditTexts[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(editText.getText().toString().trim().length() == 1) {
                        nextEditText.requestFocus();
                    }
                    if(editText.getText().toString().trim().length() == 0) {
                        if(previousEditText != null){
                            previousEditText.requestFocus();
                        }
                    }
                }
            });
        }
        passcodeEditTexts[passcodeEditTexts.length - 1].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(passcodeEditTexts[passcodeEditTexts.length - 1].getText().toString().trim().length() == 1) {
                    submitButton.requestFocus();
                }
                if(passcodeEditTexts[passcodeEditTexts.length - 1].getText().toString().trim().length() == 0){
                    passcodeEditTexts[passcodeEditTexts.length - 2].requestFocus();
                }

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_passcode, container, false);
        passcodeEditTexts = new EditText[]{
                v.findViewById(R.id.passcodeDigit1EditText),
                v.findViewById(R.id.passcodeDigit2EditText),
                v.findViewById(R.id.passcodeDigit3EditText),
                v.findViewById(R.id.passcodeDigit4EditText)
        };
        for(EditText passcodeEditText : passcodeEditTexts){
            passcodeEditText.setTransformationMethod(new MyPasswordTransformationMethod());
        }
        passcodeEditTexts[0].requestFocus();
        setOnKeyListeners();
        submitButton = v.findViewById(R.id.passcodeSubmitButton);
        submitButton.setOnClickListener(passCodeOnSubmitListener);
        return v;
    }

    public class MyPasswordTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return new PasswordCharSequence(source);
        }

        private class PasswordCharSequence implements CharSequence {
            private CharSequence mSource;
            public PasswordCharSequence(CharSequence source) {
                mSource = source; // Store char sequence
            }
            public char charAt(int index) {
                return  BIGGER_DOT; // This is the important part
            }
            public int length() {
                return mSource.length(); // Return default
            }
            public CharSequence subSequence(int start, int end) {
                return mSource.subSequence(start, end); // Return default
            }
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainScreenActivity = this.getActivity();
        token = new Token(AuthHelper.getInstance(this.getContext()).getIdToken());
        Passcode savedPasscode = new PrefHelper(getActivity().getApplicationContext()).getPasscode();
        mProgressDialog = new ProgressDialog(getContext());
        prefHelper = new PrefHelper(getActivity().getApplicationContext());
        authHelper = AuthHelper.getInstance(this.getContext());
        if(!isNewPasscodeRequired && savedPasscode.getPasscode() != null){
            enteredPasscode = savedPasscode.getPasscode();
            validatePasscode();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }

    void openFragment(Fragment fragment){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.loginSignUpContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    void validatePasscode(){
        mProgressDialog.setMessage("Validating passcode..");
        mProgressDialog.show();
        NetworkRequest networkRequest = NetworkRequest.getInstance();
        networkRequest.doGetPasscodes(token, doGetPasscodes);
    }

    View.OnClickListener passCodeOnSubmitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            enteredPasscode = "";
            for(EditText editText : passcodeEditTexts){
                if(editText.getText().toString().equals("")){
                    makeToast("Please Enter valid passcode");
                    return;
                }
                enteredPasscode += editText.getText().toString();
                validatePasscode();
            }

        }
    };
    private void dismissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void clearPasscode(){
        for(EditText editText : passcodeEditTexts){
            editText.setText("");
        }
    }


    private NetworkRequest.Callback<Passcode[]> doGetPasscodes = new NetworkRequest.Callback<Passcode[]>() {


        @Override
        public void onResponse(int responseCode, @NonNull Passcode[] passcodes) {
            if (responseCode == Constants.SUCCESS_RESPONSE_CODE) {
                boolean isValidPasscode = false;
                for (Passcode passcode : passcodes) {
                    if (passcode.getPasscode().equals(enteredPasscode)) {
                        isValidPasscode = true;
                        prefHelper.savePasscode(passcode);
                        addValidatePasscodeWorker();
                        if (isAdded()) {
                            Intent myIntent = new Intent(mainScreenActivity, MainScreen.class);
                            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(myIntent);
                            dismissDialog();
                            clearPasscode();
                            mainScreenActivity.finish();
                        }
                    }
                }
                if(!isValidPasscode) {
                    makeToast("Passcode is incorrect");
                }
                passcodeEditTexts[0].requestFocus();
            }else if(responseCode == Constants.UNAUTHORIZED_RESPONSE_CODE){
                authHelper.clear();
                makeToast("Session expired!!! Login Again..");
                openFragment(SigninFragment.newInstance("", ""));

            }else{
                makeToast("Some error occurred");
            }
        }

        @Override
        public void onError(int responseCode, String error) {
            dismissDialog();
            if(responseCode == Constants.UNAUTHORIZED_RESPONSE_CODE) {
                authHelper.clear();
                clearPasscode();
                makeToast("Session expired!!! Login Again..");
                openFragment(SigninFragment.newInstance("", ""));
            }else if(responseCode == Constants.INTERNET_UNAVAILABLE){
                makeToast(error);
            }else{
                makeToast("Some error occurred");
            }
        }

        @Override
        public Class<Passcode[]> type() {
            return Passcode[].class;
        }

    };

    public void addValidatePasscodeWorker() {
        PeriodicWorkRequest workRequest;
        Data data = new Data.Builder().putString(PasscodeFragment.MESSAGE_STATUS, prefHelper.getPasscode().getPasscode()).build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            workRequest = new PeriodicWorkRequest.Builder(PasscodeValidator.class, 15, TimeUnit.MINUTES).setInputData(data).build();
            final WorkManager mWorkManager = WorkManager.getInstance();
            mWorkManager.enqueueUniquePeriodicWork("passcodeValidator",
                    ExistingPeriodicWorkPolicy.REPLACE,
                    workRequest);
            mWorkManager.getWorkInfoByIdLiveData(workRequest.getId()).observe(this, new Observer<WorkInfo>() {
                @Override
                public void onChanged(@Nullable WorkInfo workInfo) {
                    if (workInfo != null) {
                        WorkInfo.State state = workInfo.getState();
                        if (state == WorkInfo.State.SUCCEEDED) {
                        } else {
//                            prefHelper.clearPasscodes();
                            mWorkManager.cancelAllWork();
                            openFragment(PasscodeFragment.newInstance("true", ""));
                        }
                    }
                }
            });
        }
    }
}