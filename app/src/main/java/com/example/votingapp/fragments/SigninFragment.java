package com.example.votingapp.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.votingapp.ApiLib.NetworkRequest;
import com.example.votingapp.R;
import com.example.votingapp.constants.Constants;
import com.example.votingapp.model.Token;
import com.example.votingapp.model.User;
import com.example.votingapp.screens.LoginSignupScreen;
import com.example.votingapp.screens.MainScreen;
import com.example.votingapp.utils.AuthHelper;
import com.example.votingapp.utils.PrefHelper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SigninFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SigninFragment extends BaseFragment {


    private EditText mEditEmail;
    private EditText mEditPassword;

    private TextView forgotPasswordLink;
    private TextView signUpLink;
    private Button mButtonAction;

    private ProgressDialog mProgressDialog;
    private AuthHelper mAuthHelper;

    private PrefHelper prefHelper;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SigninFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SigninFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SigninFragment newInstance(String param1, String param2) {
        SigninFragment fragment = new SigninFragment();
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

        View v =   inflater.inflate(R.layout.fragment_signin, container, false);
        // Inflate the layout for this fragment
        mProgressDialog = new ProgressDialog(getContext());

        prefHelper = new PrefHelper(getActivity().getApplicationContext());

        mAuthHelper = AuthHelper.getInstance(getContext());
        mEditEmail = (EditText) v.findViewById(R.id.username);
        mEditPassword = (EditText) v.findViewById(R.id.password);
        mButtonAction = (Button) v.findViewById(R.id.loginButton);

        forgotPasswordLink = v.findViewById(R.id.forgotPasswordTextView);
        signUpLink = v.findViewById(R.id.signUpTextView);
        setupView();
        return v;
    }

    View.OnClickListener forgotPasswordListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openFragment(ForgotPassword.newInstance("", ""));
        }
    };

    View.OnClickListener signUpListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openFragment(SignUpFragment.newInstance("", ""));
        }
    };

    private void setupView() {
        mButtonAction.setOnClickListener(doLoginClickListener);
        mEditEmail.setText(prefHelper.getUserName());
        mEditPassword.setText(prefHelper.getPassword());
        forgotPasswordLink.setOnClickListener(forgotPasswordListener);
        signUpLink.setOnClickListener(signUpListener);
    }

    private void doLogin() {
        String username = getUsernameText();
        String password = getPasswordText();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            makeToast("Please fill all the fields");
            return;
        }
        mProgressDialog.setMessage(getString(R.string.progress_login));
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        NetworkRequest request =  NetworkRequest.getInstance();
        request.doLogin(new User(username, password), mLoginCallback);
    }



    /**
     * Sign up the user and navigate to profile screen
     */

    private String getUsernameText() {
        return mEditEmail.getText().toString().trim();
    }

    private String getPasswordText() {
        return mEditPassword.getText().toString().trim();
    }


    /**
     * Save session details and navigates to the quotes activity
     * @param token - {@link Token} received on login or signup
     */
    private void saveSessionDetails(@NonNull Token token) {
        prefHelper.saveCredentials(getUsernameText(), getPasswordText());
        mAuthHelper.setIdToken(token);
        openFragment(PasscodeFragment.newInstance("", ""));

    }

    /**
     * Callback for login
     */
    private NetworkRequest.Callback<Token> mLoginCallback = new NetworkRequest.Callback<Token>() {
        @Override
        public void onResponse(int responseCode, @NonNull Token response) {
            dismissDialog();
            if(responseCode == Constants.SUCCESS_RESPONSE_CODE) {
                makeToast("Login Successful");
                // save token and go to profile page
                saveSessionDetails(response);
            }else{
                makeToast("Invalid Username and Password...");
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
        public Class<Token> type() {
            return Token.class;
        }

    };

    /**
     * Dismiss the dialog if it's showing
     */
    private void dismissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }

    /**
     * Click listener to invoke login
     */
    private final View.OnClickListener doLoginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            doLogin();
        }
    };
}