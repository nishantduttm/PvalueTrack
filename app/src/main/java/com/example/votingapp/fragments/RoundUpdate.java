package com.example.votingapp.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.votingapp.ApiLib.NetworkRequest;
import com.example.votingapp.R;
import com.example.votingapp.UIHelper.AutoCompleteTextViews;
import com.example.votingapp.adapter.AutoSuggestAdapter;
import com.example.votingapp.db.LogDbHelper;
import com.example.votingapp.constants.Constants;
import com.example.votingapp.model.AssemblyConstituency;
import com.example.votingapp.model.Candidate;
import com.example.votingapp.model.LastRoundData;
import com.example.votingapp.model.LogEntry;
import com.example.votingapp.model.Passcode;
import com.example.votingapp.model.RoundUpdateBody;
import com.example.votingapp.model.Token;
import com.example.votingapp.screens.LoginSignupScreen;
import com.example.votingapp.utils.ACHelper;
import com.example.votingapp.utils.AuthHelper;
import com.example.votingapp.utils.CandidatesListHelper;
import com.example.votingapp.utils.KeyboardUtil;
import com.example.votingapp.utils.PrefHelper;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoundUpdate#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoundUpdate extends BaseFragment {


    Token token;
    private ProgressDialog mProgressDialog;

    private LastRoundData[] lastRoundDataList;


    AutoCompleteTextView acTextView;
    AutoCompleteTextView acNameTextView;
    EditText roundNoTextView;
    AutoCompleteTextView pCodeTextView;
    AutoCompleteTextView pNameTextView;
    AutoCompleteTextView candidateNameTextView;


    TextView roundNoLabel;
    Button submitButton;


    LogDbHelper logDbHelper;


    AutoCompleteTextViews autoCompleteTextViews;

    private PrefHelper prefHelper;

    private View view;


    private static final String ARG_PARAM2 = "param2";

    private String electionCode;
    private String mParam2;

    Activity activity;

    AuthHelper authHelper;

    FirebaseCrashlytics crashlytics;


   CandidatesListHelper candidatesListHelper;

   ACHelper acHelper;

    public RoundUpdate() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RoundUpdate.
     */
    // TODO: Rename and change types and number of parameters
    public static RoundUpdate newInstance(String param1, String param2) {
        RoundUpdate fragment = new RoundUpdate();
        Bundle args = new Bundle();
        args.putString(Constants.ELECTION_CODE, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_round_update, container, false);
        mProgressDialog = new ProgressDialog(this.getContext());
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        prefHelper = new PrefHelper(getActivity().getApplicationContext());
        Passcode passcode =  prefHelper.getPasscode();
        crashlytics = FirebaseCrashlytics.getInstance();
        token = new Token(AuthHelper.getInstance(RoundUpdate.this.getContext()).getIdToken());
        logDbHelper = new LogDbHelper(getContext());
        authHelper = AuthHelper.getInstance(RoundUpdate.this.getContext());
        if(passcode.getElectionCode() == null){
            log("info", "Starting passcode fragment as passcode is null");
            Intent myIntent = new Intent(getActivity(), LoginSignupScreen.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(myIntent);
            getActivity().finish();
        }
        new KeyboardUtil(getActivity(),view.findViewById(R.id.main_form));
        electionCode = passcode.getElectionCode();
        getACList();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    protected void setRoundNoText() {
        int roundNo = getRoundNo();
        roundNoLabel.setVisibility(View.VISIBLE);
        if (roundNo == -1) {
            roundNoLabel.setText("Rounds haven't started");
        } else {
            roundNoLabel.setText("Last Round " + roundNo + "");
        }
    }


    protected void setUpAcCodeAndAcNameDropDown(){
        if(acHelper.getACodeList().isEmpty()){
            makeToast("Unable to get assembly constituency details");
            return;
        }
        AutoSuggestAdapter acCodeAdapter = new AutoSuggestAdapter(this.activity, R.layout.list_item_1, acHelper.getACodeList());
        acTextView.setAdapter(acCodeAdapter);
        AutoSuggestAdapter acNameAdapter = new AutoSuggestAdapter(this.activity, R.layout.list_item_1, acHelper.getACNameList());
        acNameTextView.setAdapter(acNameAdapter);
        if (acNameTextView.isFocused() && autoCompleteTextViews.isValidACNameText()) {
            autoCompleteTextViews.setACText(acHelper.findACCodeByACName(autoCompleteTextViews.getACName()));
        } else {
            autoCompleteTextViews.setACNameText(acHelper.findACNameByACCode(autoCompleteTextViews.getACCode()));
        }
    }


    protected void setUpAutoCompleteTextViews() {
        Trace trace = FirebasePerformance.startTrace("SetupAutoCompleteViews");
        if(candidatesListHelper == null){
            AutoSuggestAdapter partyNameAdapter = new AutoSuggestAdapter(this.activity, R.layout.list_item_1, Arrays.asList("No Result"));
            pNameTextView.setAdapter(partyNameAdapter);
            AutoSuggestAdapter partyCodeAdapter = new AutoSuggestAdapter(this.activity, R.layout.list_item_1, Arrays.asList("No Result"));
            pCodeTextView.setAdapter(partyCodeAdapter);
            AutoSuggestAdapter candidateNameAdapter = new AutoSuggestAdapter(this.activity, R.layout.list_item_1, Arrays.asList("No Result"));
            candidateNameTextView.setAdapter(candidateNameAdapter);
            return;
        }
        if (autoCompleteTextViews.isValidACText() || autoCompleteTextViews.isValidACNameText()) {
            if ((pCodeTextView.isFocused() || pNameTextView.isFocused()) && (autoCompleteTextViews.isValidPartyName() || autoCompleteTextViews.isValidPartyCode())) {
                if (pCodeTextView.isFocused() && autoCompleteTextViews.isValidPartyCode()) {
                    autoCompleteTextViews.setPartyNameText(candidatesListHelper.findPartyNameByPartyCode(autoCompleteTextViews.getPartyCode()));
                    autoCompleteTextViews.setCandidateNameText(candidatesListHelper.findCandidateNamesByPartyCode(autoCompleteTextViews.getPartyCode()));
                } else {
                    if (autoCompleteTextViews.isValidCandidateName()) {
                        List<String> partyCodes = candidatesListHelper.findPartyCodesByCandidateNameAndPartyName(autoCompleteTextViews.getPartyName(), autoCompleteTextViews.getCandidateName());
                        if (partyCodes.size() == 1) {
                            pCodeTextView.setText(partyCodes.get(0));
                        } else {
                            AutoSuggestAdapter partyCodeAdapter = new AutoSuggestAdapter(this.activity, R.layout.list_item_1, partyCodes);
                            pCodeTextView.setAdapter(partyCodeAdapter);
                        }
                    } else {
                        List<String> pCodes = candidatesListHelper.findPartyCodeByPartyName(autoCompleteTextViews.getPartyName());
                        if (pCodes.size() == 1) {
                            pCodeTextView.setText(pCodes.get(0));
                            String candidate = candidatesListHelper.findCandidateNamesByPartyCode(autoCompleteTextViews.getPartyCode());
                            candidateNameTextView.setText(candidate);
                        } else {
                            AutoSuggestAdapter partyCodeAdapter = new AutoSuggestAdapter(this.activity, R.layout.list_item_1, pCodes);
                            pCodeTextView.setAdapter(partyCodeAdapter);
                            AutoSuggestAdapter candidateNameAdapter = new AutoSuggestAdapter(this.activity, R.layout.list_item_1, candidatesListHelper.findCandidateNamesByPartyName(autoCompleteTextViews.getPartyName()));
                            candidateNameTextView.setAdapter(candidateNameAdapter);
                        }

                    }
                }
            } else if (candidateNameTextView.isFocused() && autoCompleteTextViews.isValidCandidateName()) {
                List<String> partyNames = candidatesListHelper.findPartyNamesByCandidateName(autoCompleteTextViews.getCandidateName());
                List<String> partyCodes = candidatesListHelper.findPartyCodesByCandidateName(autoCompleteTextViews.getCandidateName());
                if (partyNames.size() == 1) {
                    pNameTextView.setText(partyNames.get(0));
                } else {
                    AutoSuggestAdapter partyNameAdapter = new AutoSuggestAdapter(this.activity, R.layout.list_item_1, partyNames);
                    pNameTextView.setAdapter(partyNameAdapter);
                }
                if (partyCodes.size() == 1) {
                    pCodeTextView.setText(partyCodes.get(0));
                } else {
                    AutoSuggestAdapter partyCodeAdapter = new AutoSuggestAdapter(this.activity, R.layout.list_item_1, partyCodes);
                    pCodeTextView.setAdapter(partyCodeAdapter);
                }
            } else {
                AutoSuggestAdapter partyNameAdapter = new AutoSuggestAdapter(this.activity, R.layout.list_item_1, candidatesListHelper.getPartyNames());
                pNameTextView.setAdapter(partyNameAdapter);
                AutoSuggestAdapter partyCodeAdapter = new AutoSuggestAdapter(this.activity, R.layout.list_item_1, candidatesListHelper.getPartCodes());
                pCodeTextView.setAdapter(partyCodeAdapter);
                AutoSuggestAdapter candidateNameAdapter = new AutoSuggestAdapter(this.activity, R.layout.list_item_1, candidatesListHelper.getCandidateNames());
                candidateNameTextView.setAdapter(candidateNameAdapter);
            }

        } else {
            AutoSuggestAdapter partyNameAdapter = new AutoSuggestAdapter(this.activity, R.layout.list_item_1, Arrays.asList("No Result"));
            pNameTextView.setAdapter(partyNameAdapter);
            AutoSuggestAdapter partyCodeAdapter = new AutoSuggestAdapter(this.activity, R.layout.list_item_1, Arrays.asList("No Result"));
            pCodeTextView.setAdapter(partyCodeAdapter);
            AutoSuggestAdapter candidateNameAdapter = new AutoSuggestAdapter(this.activity, R.layout.list_item_1, Arrays.asList("No Result"));
            candidateNameTextView.setAdapter(candidateNameAdapter);
        }
        if (electionCode != null && autoCompleteTextViews.isValidACText()) {
            getLastRoundDataList();
        }
        trace.stop();
    }


    protected void setUpViews() {
        roundNoLabel = view.findViewById(R.id.roundDetailsLabel);
        acTextView = (AutoCompleteTextView) view.findViewById(R.id.acCodeAutoCompleteTextView);
        acNameTextView = (AutoCompleteTextView) view.findViewById(R.id.acNameAutoCompleteTextView);
        ;
        roundNoTextView = (EditText) view.findViewById(R.id.roundNoEditText);
        pCodeTextView = (AutoCompleteTextView) view.findViewById(R.id.pCodeAutoCompleteTextView);
        pNameTextView = (AutoCompleteTextView) view.findViewById(R.id.pNameAutoCompleteTextView);
        candidateNameTextView = (AutoCompleteTextView) view.findViewById(R.id.candidateNameAutoCompleteTextView);
        submitButton = (Button) view.findViewById(R.id.submitButton);

        pNameTextView.setThreshold(0);
        acTextView.setThreshold(0);
        acNameTextView.setThreshold(0);
        pCodeTextView.setThreshold(0);
        pNameTextView.setThreshold(0);
        candidateNameTextView.setThreshold(0);

        submitButton.setOnClickListener(doSubmitListener);


        setUpOnFocusChangeListeners();
        seUpOnItemClickListeners();
    }


    protected void seUpOnItemClickListeners() {
        acTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                acNameTextView.setText("");
                setUpAcCodeAndAcNameDropDown();

                getCandidates();
                getLastRoundDataList();
            }
        });
        acNameTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                acTextView.setText("");
                setUpAcCodeAndAcNameDropDown();
                getCandidates();
                getLastRoundDataList();
            }
        });
        pCodeTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pNameTextView.setText("");
                candidateNameTextView.setText("");
                setUpAutoCompleteTextViews();
            }
        });
        pNameTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pCodeTextView.setText("");
                candidateNameTextView.setText("");
                setUpAutoCompleteTextViews();
            }
        });
        candidateNameTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setUpAutoCompleteTextViews();
            }
        });
    }

    protected void setUpOnFocusChangeListeners() {
        acTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    prefHelper.clearACData();
                    acTextView.showDropDown();
                    acNameTextView.setText("");
                    pCodeTextView.setText("");
                    pNameTextView.setText("");
                    roundNoTextView.setText("");
                    candidateNameTextView.setText("");
                }
                setUpAutoCompleteTextViews();
            }
        });

        acNameTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    prefHelper.clearACData();
                    acNameTextView.showDropDown();
                    acTextView.setText("");
                    pCodeTextView.setText("");
                    pNameTextView.setText("");
                    roundNoTextView.setText("");
                    candidateNameTextView.setText("");
                }
                setUpAutoCompleteTextViews();
            }
        });

        pCodeTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    pNameTextView.setText("");
                    candidateNameTextView.setText("");
                    setUpAutoCompleteTextViews();
                    pCodeTextView.showDropDown();
                }
                roundNoTextView.setText("");
                setUpAutoCompleteTextViews();
            }
        });

        pNameTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    pCodeTextView.setText("");
                    candidateNameTextView.setText("");
                    setUpAutoCompleteTextViews();
                    pNameTextView.showDropDown();
                }
                roundNoTextView.setText("");
            }
        });


        candidateNameTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setUpAutoCompleteTextViews();
                    candidateNameTextView.showDropDown();
                }
            }
        });

    }


    public void clearTextViews() {
        acTextView.setText("");
        acNameTextView.setText("");
        roundNoTextView.setText("");
        pCodeTextView.setText("");
        pNameTextView.setText("");
        candidateNameTextView.setText("");
        roundNoLabel.setVisibility(View.INVISIBLE);
    }

    public int getRoundNo() {
        if (lastRoundDataList != null) {
            for (LastRoundData lastRoundData : lastRoundDataList) {
                if (lastRoundData.getAccode().equals(autoCompleteTextViews.getACCode()) && lastRoundData.getEcode().equals(electionCode)) {
                    return lastRoundData.getRound();
                }
            }
        }
        return -1;
    }

    private final View.OnClickListener doReEntryListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            clearTextViews();
        }
    };

    private final View.OnClickListener doRefreshListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            clearTextViews();
            getCandidates();
        }
    };

    private final View.OnClickListener doSubmitListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            postRoundUpdate();
        }
    };

    public void getCandidates() {
        candidatesListHelper = null;
        mProgressDialog.setMessage("Getting candidate details for Assembly Constituency");
        mProgressDialog.show();
        NetworkRequest request = NetworkRequest.getInstance();
        request.doGetCandidates(token, doGetCandidatesCallback, electionCode, autoCompleteTextViews.getACCode());
    }

    public void getACList() {
        mProgressDialog.setMessage("Refreshing constituency details...Please Wait...");
        mProgressDialog.show();
        NetworkRequest request = NetworkRequest.getInstance();
        request.doGetACList(token, electionCode, doGetAcList);
    }

    private void getLastRoundDataList() {
        NetworkRequest request = NetworkRequest.getInstance();
        if(autoCompleteTextViews.isValidACText()) {
            request.doGetLastRoundData(token, electionCode, autoCompleteTextViews.getACCode(), doGetLastRoundData);
        }
    }


    public RoundUpdateBody prepareUpdate() {
        RoundUpdateBody roundUpdate = null;
        try {
            roundUpdate = new RoundUpdateBody();
            if (autoCompleteTextViews.isValidCandidateName() && autoCompleteTextViews.isValidACNameText() && autoCompleteTextViews.isValidPartyCode() && autoCompleteTextViews.isValidPartyName() && autoCompleteTextViews.isValidACText()) {
                roundUpdate.setAssemblyConstitutionCode(autoCompleteTextViews.getACCode());
                roundUpdate.setPartyCode(autoCompleteTextViews.getPartyCode());
                roundUpdate.setCandidateCode(candidatesListHelper.findCandidateCodeByCandidateNameAndPartyCode(autoCompleteTextViews.getCandidateName(),autoCompleteTextViews.getPartyCode()));
                roundUpdate.setElectionCode(electionCode);
                roundUpdate.setRound(autoCompleteTextViews.getRoundNo());
            } else {
                makeToast("Invalid Value");
            }
        } catch (Exception e) {
            makeToast("Invalid Value");
        }
        return roundUpdate;
    }

    private void postRoundUpdate() {
        if(autoCompleteTextViews == null){
            return;
        }
        if(getRoundNo() >= 90){
            roundNoTextView.setError("Round no is already 90");
            return;
        }
        if (autoCompleteTextViews.getRoundNo() <= getRoundNo()) {
            roundNoTextView.setError("Round no must be greater than " + getRoundNo());
            return;
        }
        if(autoCompleteTextViews.getRoundNo() > 90){
            roundNoTextView.setError("Round no must be less than or equal to 90");
            return;
        }
        mProgressDialog.setMessage("Updating Round...Please Wait...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        NetworkRequest request = NetworkRequest.getInstance();
        request.doUpdateRound(token, prepareUpdate(), dpPostRoundUpdate);
    }


    private NetworkRequest.Callback<Candidate[]> doGetCandidatesCallback = new NetworkRequest.Callback<Candidate[]>() {


        @Override
        public void onResponse(int responseCode, @NonNull Candidate[] candidates) {
            log("info","onResponse: "+Arrays.toString(candidates));
            mProgressDialog.dismiss();
            candidatesListHelper = new CandidatesListHelper(candidates);
            autoCompleteTextViews = new AutoCompleteTextViews(acTextView, acNameTextView, roundNoTextView, pCodeTextView, pNameTextView, candidateNameTextView, candidatesListHelper, acHelper);
            setUpAutoCompleteTextViews();
        }

        @Override
        public void onError(int responseCode, String error) {
            mProgressDialog.dismiss();
            if (responseCode == Constants.UNAUTHORIZED_RESPONSE_CODE) {
                makeToast("Session expired!!! Login Again..");
                // clear saved token
                authHelper.clear();
                openLoginActivity();
            }else if(responseCode == Constants.INTERNET_UNAVAILABLE){
                makeToast(error);
            } else {
                makeToast("Not able to fetch candidates..");
            }
        }

        @Override
        public Class<Candidate[]> type() {
            return Candidate[].class;
        }

    };

    private NetworkRequest.Callback<String> dpPostRoundUpdate = new NetworkRequest.Callback<String>() {


        @Override
        public void onResponse(int responseCode, @NonNull String response) {
            mProgressDialog.dismiss();
            log("info", "onResponse Round Update: " + response.toString());
            try {
                if (responseCode == Constants.SUCCESS_RESPONSE_CODE) {
                    prefHelper.saveACDetails(autoCompleteTextViews.getACCode(), autoCompleteTextViews.getACName());
                    logDbHelper.saveLog(new LogEntry(
                            autoCompleteTextViews.getACCode(),
                            autoCompleteTextViews.getACName(),
                            autoCompleteTextViews.getPartyName(),
                            autoCompleteTextViews.getPartyCode(),
                            autoCompleteTextViews.getCandidateName(),
                            autoCompleteTextViews.getRoundNo(),
                            prefHelper.getUserName(),
                            electionCode));
                    clearTextViews();
                    makeToast(response);
                } else if (responseCode == Constants.UNAUTHORIZED_RESPONSE_CODE) {
                    makeToast("Session expired!!! Login Again..");
                    authHelper.clear();
                    openLoginActivity();
                } else {
                    if (response != null && !response.isBlank()) {
                        makeToast(response);
                    } else {
                        makeToast("Something went wrong");
                    }
                }
            }catch (Exception  e){
                log("info", "Exception occurred" + e);
            }

        }

        @Override
        public void onError(int responseCode, String error) {
            mProgressDialog.dismiss();
            if (responseCode == Constants.UNAUTHORIZED_RESPONSE_CODE) {
                makeToast("Session expired!!! Login Again..");
                openLoginActivity();
            }else if(responseCode == Constants.INTERNET_UNAVAILABLE){
                makeToast(error);
            }else {
                makeToast("Something went wrong...");
            }
        }

        @Override
        public Class<String> type() {
            return String.class;
        }

    };

    private NetworkRequest.Callback<LastRoundData[]> doGetLastRoundData = new NetworkRequest.Callback<LastRoundData[]>() {


        @Override
        public void onResponse(int responseCode, @NonNull LastRoundData[] lastRoundDataList) {
            if (responseCode == Constants.UNAUTHORIZED_RESPONSE_CODE) {
                makeToast("Session expired!!! Login Again..");
                // clear saved token
                openLoginActivity();

            }
            log("info", "onResponse: " + Arrays.toString(lastRoundDataList));
            RoundUpdate.this.lastRoundDataList = lastRoundDataList;
            setRoundNoText();
        }

        @Override
        public void onError(int responseCode, String error) {
            mProgressDialog.dismiss();
            if (responseCode == Constants.UNAUTHORIZED_RESPONSE_CODE) {
                makeToast("Session expired!!! Login Again..");
                // clear saved token
                authHelper.clear();
                openLoginActivity();
            } else if(responseCode == Constants.INTERNET_UNAVAILABLE){
                makeToast(error);
            }else {
                makeToast("Unable to fetch last round information..");
            }
        }

        @Override
        public Class<LastRoundData[]> type() {
            return LastRoundData[].class;
        }

    };

    private NetworkRequest.Callback<AssemblyConstituency[]> doGetAcList = new NetworkRequest.Callback<AssemblyConstituency[]>() {


        @Override
        public void onResponse(int responseCode, @NonNull AssemblyConstituency[] assemblyConstituencies) {
            mProgressDialog.dismiss();
            if (responseCode == Constants.UNAUTHORIZED_RESPONSE_CODE) {
                makeToast("Session expired!!! Login Again..");
                authHelper.clear();
                openLoginActivity();
            }
            log("info", "onResponse: " + Arrays.toString(assemblyConstituencies));
            RoundUpdate.this.acHelper = new ACHelper(assemblyConstituencies);
            setUpViews();
            autoCompleteTextViews = new AutoCompleteTextViews(acTextView, acNameTextView, roundNoTextView, pCodeTextView, pNameTextView, candidateNameTextView, candidatesListHelper, acHelper);
            setUpAcCodeAndAcNameDropDown();
        }

        @Override
        public void onError(int responseCode, String error) {
            mProgressDialog.dismiss();
            if (responseCode == Constants.UNAUTHORIZED_RESPONSE_CODE) {
                makeToast("Session expired!!! Login Again..");
                authHelper.clear();
                openLoginActivity();
            } else if(responseCode == Constants.INTERNET_UNAVAILABLE){
                makeToast(error);
            }else {
                makeToast("Unable to fetch AC Details ");
            }
            setUpViews();
        }

        @Override
        public Class<AssemblyConstituency[]> type() {
            return AssemblyConstituency[].class;
        }

    };
}