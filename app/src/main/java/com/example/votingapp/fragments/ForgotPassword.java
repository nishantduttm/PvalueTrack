package com.example.votingapp.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.votingapp.ApiLib.NetworkRequest;
import com.example.votingapp.R;
import com.example.votingapp.constants.Constants;
import com.example.votingapp.model.LastRoundData;
import com.example.votingapp.model.Token;
import com.example.votingapp.screens.LoginSignupScreen;
import com.example.votingapp.utils.AuthHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForgotPassword#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForgotPassword extends BaseFragment {
    EditText userNameEditText;

    String enteredUserName;

    Button submitButton;

    Token token;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ProgressDialog mProgressDialog;


    public ForgotPassword() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForgotPassword.
     */
    // TODO: Rename and change types and number of parameters
    public static ForgotPassword newInstance(String param1, String param2) {
        ForgotPassword fragment = new ForgotPassword();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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

    @Override
    public void onStart() {
        super.onStart();

    }


    private void dismissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        token = new Token(AuthHelper.getInstance(this.getContext()).getIdToken());
        View v = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        userNameEditText = v.findViewById(R.id.userNameEditText);
        submitButton = v.findViewById(R.id.resetPasswordButton);
        submitButton.setOnClickListener(doForgotPassword);
        mProgressDialog = new ProgressDialog(this.getContext());
        return v;
    }

    View.OnClickListener doForgotPassword = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            enteredUserName = userNameEditText.getText().toString().trim();
            mProgressDialog.setMessage("Sending reset link..Please wait");
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
            NetworkRequest networkRequest = NetworkRequest.getInstance();
            networkRequest.doUpdatePassword(token, enteredUserName, doForgotPasswordCallback);
        }
    };

    void openFragment(Fragment fragment){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.loginSignUpContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private NetworkRequest.Callback<String> doForgotPasswordCallback = new NetworkRequest.Callback<String>() {


        @Override
        public void onResponse(int responseCode, @NonNull String response) {
            dismissDialog();
           if(responseCode == Constants.SUCCESS_RESPONSE_CODE){
               makeToast("A password reset link has been sent to your mail");
               openFragment(SigninFragment.newInstance("", ""));
           }else{
               makeToast("Something went wrong" + response);
           }
        }

        @Override
        public void onError(int responseCode, String error) {
            dismissDialog();
            if(responseCode == Constants.INTERNET_UNAVAILABLE){
                makeToast(error);
            }else{
                makeToast("Something went wrong");
            }
        }

        @Override
        public Class<String> type() {
            return String.class;
        }

    };
}