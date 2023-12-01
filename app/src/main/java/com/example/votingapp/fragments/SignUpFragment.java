package com.example.votingapp.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.votingapp.ApiLib.NetworkRequest;
import com.example.votingapp.R;
import com.example.votingapp.constants.Constants;
import com.example.votingapp.model.Token;
import com.example.votingapp.model.User;
import com.example.votingapp.model.UserSignUp;
import com.example.votingapp.utils.AuthHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends BaseFragment {

    EditText emailEditText;
    EditText passwordEditText;
    EditText firstnameEditText;
    EditText lastnameEditText;
    EditText phoneEditText;
    Button submitButton;

    Button signInButton;

    ProgressDialog progressDialog;


    String firstName;
    String lastName;
    String email;
    String password;
    String phone;

    Token token;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUpFragment newInstance(String param1, String param2) {
        SignUpFragment fragment = new SignUpFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_sign_up, container, false);
        emailEditText = v.findViewById(R.id.emailEditText);
        firstnameEditText = v.findViewById(R.id.firstNameEditText);
        lastnameEditText = v.findViewById(R.id.lastNameEditText);
        phoneEditText = v.findViewById(R.id.phoneEditText);
        passwordEditText = v.findViewById(R.id.passwordEditText);
        submitButton = v.findViewById(R.id.signupButton);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        token = new Token(AuthHelper.getInstance(this.getContext()).getIdToken());
        submitButton.setOnClickListener(doSignUp);
        signInButton = v.findViewById(R.id.signInButton);
        signInButton.setOnClickListener(doSignIn);
        return v;
    }


    View.OnClickListener doSignUp = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!isValidateDetails()){
                makeToast("Please enter valid details");
            }else{
                email = emailEditText.getText().toString().trim();
                phone = phoneEditText.getText().toString().trim();
                firstName = firstnameEditText.getText().toString();
                lastName = lastnameEditText.getText().toString();
                password = passwordEditText.getText().toString();
                UserSignUp userSignUp = new UserSignUp(firstName, lastName, password, email, phone);
                NetworkRequest networkRequest =  NetworkRequest.getInstance();
                progressDialog.setMessage("Signing Up..");
                progressDialog.show();
                networkRequest.doSignUp(token,userSignUp, mSignUpCallback);
            }
        }
    };

    View.OnClickListener doSignIn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openFragment(SigninFragment.newInstance("", ""));
        }
    };

    private void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public  boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();

    }

    boolean isValidateDetails(){
        String firstName = firstnameEditText.getText().toString();
        if (TextUtils.isEmpty(firstName)) {
            firstnameEditText.setError("Please enter valid first name");
            firstnameEditText.requestFocus();
            return false;
        }
        String lastName = lastnameEditText.getText().toString();
        if (TextUtils.isEmpty(lastName)) {
            lastnameEditText.setError("Please enter valid last name");
            lastnameEditText.requestFocus();
            return false;
        }
        String email = emailEditText.getText().toString().trim();
        if(TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Please enter valid email address.");
            emailEditText.requestFocus();
            return false;
        }
        String phone = phoneEditText.getText().toString().trim();
        if(TextUtils.isEmpty(phone) || !Patterns.PHONE.matcher(phone).matches()){
            phoneEditText.setError("Please enter valid phone no.");
            phoneEditText.requestFocus();
            return false;
        }
        String password = passwordEditText.getText().toString();
        if(TextUtils.isEmpty(password) || !isValidPassword(password)){
            passwordEditText.setError("Password must contain minimum 8 characters at least 1 Alphabet, 1 Number and 1 Special Character");
            passwordEditText.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }

    private NetworkRequest.Callback<String> mSignUpCallback = new NetworkRequest.Callback<String>() {
        @Override
        public void onResponse(int responseCode, @NonNull String response) {
            dismissDialog();
            if(responseCode == Constants.SUCCESS_RESPONSE_CODE) {
                makeToast("Signup successful");
                openFragment(SigninFragment.newInstance("", ""));
            }else{
                makeToast(response);
            }
        }

        @Override
        public void onError(int responseCode, String error) {
            dismissDialog();
            if(responseCode == Constants.INTERNET_UNAVAILABLE){
                makeToast(error);
            }else {
                Log.d("ERROR", String.format("onError: %s", error));
                makeToast("Some error occurred");
            }
        }

        @Override
        public Class<String> type() {
            return String.class;
        }

    };
}