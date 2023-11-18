package com.example.votingapp.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.work.PeriodicWorkRequest;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.votingapp.ApiLib.NetworkRequest;
import com.example.votingapp.R;
import com.example.votingapp.UIHelper.AutoCompleteTextViews;
import com.example.votingapp.adapter.AutoSuggestAdapter;
import com.example.votingapp.db.CandidateDbHelper;
import com.example.votingapp.db.LogDbHelper;
import com.example.votingapp.constants.Constants;
import com.example.votingapp.model.Candidate;
import com.example.votingapp.model.LastRoundData;
import com.example.votingapp.model.LogEntry;
import com.example.votingapp.model.RoundUpdateBody;
import com.example.votingapp.model.Token;
import com.example.votingapp.utils.AuthHelper;
import com.example.votingapp.utils.KeyboardUtil;
import com.example.votingapp.utils.PrefHelper;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoundUpdate#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoundUpdate extends BaseFragment {

    PeriodicWorkRequest workRequest;

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

    CandidateDbHelper candidateDbHelper;

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
        crashlytics = FirebaseCrashlytics.getInstance();
        token = new Token(AuthHelper.getInstance(this.getContext()).getIdToken());
        candidateDbHelper = new CandidateDbHelper(getContext());
        logDbHelper = new LogDbHelper(getContext());
        prefHelper = new PrefHelper(this.getContext());
        electionCode = prefHelper.getPasscode().getElectionCode();
        authHelper = AuthHelper.getInstance(this.getContext());
        crashlytics.setCustomKey("isInitialized Passcode:", prefHelper.getPasscode().toString());
        if (!candidateDbHelper.isInitialized(electionCode)) {
            getCandidates();
        } else {
            setUpViews();
            autoCompleteTextViews = new AutoCompleteTextViews(acTextView, acNameTextView, roundNoTextView, pCodeTextView, pNameTextView, candidateNameTextView, candidateDbHelper);
            setUpAutoCompleteTextViews();
        }
        new KeyboardUtil(getActivity(),view.findViewById(R.id.main_form));
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        NetworkRequest.getInstance().cancelOngoingRequests();
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


    protected void setUpAutoCompleteTextViews() {
        AutoSuggestAdapter acCodeAdapter = new AutoSuggestAdapter(this.activity, R.layout.list_item_1, candidateDbHelper.getAllACCodes(electionCode));
        acTextView.setAdapter(acCodeAdapter);
        AutoSuggestAdapter acNameAdapter = new AutoSuggestAdapter(this.activity, R.layout.list_item_1, candidateDbHelper.getAllACNames(electionCode));
        acNameTextView.setAdapter(acNameAdapter);
        if (candidateDbHelper.isValidACName(prefHelper.getACName()) && candidateDbHelper.isValidACCode(prefHelper.getACCode())) {
            acTextView.setText(prefHelper.getACCode());
            acNameTextView.setText(prefHelper.getACName());
        }
        if (acNameTextView.isFocused() && autoCompleteTextViews.isValidACNameText()) {
            autoCompleteTextViews.setACText(candidateDbHelper.findACCodeByACName(autoCompleteTextViews.getACName()));
        } else {
            autoCompleteTextViews.setACNameText(candidateDbHelper.findACNameByACCode(autoCompleteTextViews.getACCode()));
        }
        if (autoCompleteTextViews.isValidACText() || autoCompleteTextViews.isValidACNameText()) {
            if ((pCodeTextView.isFocused() || pNameTextView.isFocused()) && (autoCompleteTextViews.isValidPartyName() || autoCompleteTextViews.isValidPartyCode())) {
                if (pCodeTextView.isFocused() && autoCompleteTextViews.isValidPartyCode()) {
                    autoCompleteTextViews.setPartyNameText(candidateDbHelper.findPartyNameByPartyCode(autoCompleteTextViews.getPartyCode()));
                    autoCompleteTextViews.setCandidateNameText(candidateDbHelper.findCandidateNamesByPartyCodeAndACCode(autoCompleteTextViews.getPartyCode(), autoCompleteTextViews.getACCode()));
                } else {
                    if (autoCompleteTextViews.isValidCandidateName()) {
                        List<String> partyCodes = candidateDbHelper.findPartyCodesByCandidateNameAndPartyName(autoCompleteTextViews.getPartyName(), autoCompleteTextViews.getCandidateName());
                        if (partyCodes.size() == 1) {
                            pCodeTextView.setText(partyCodes.get(0));
                        } else {
                            AutoSuggestAdapter partyCodeAdapter = new AutoSuggestAdapter(this.getContext(), R.layout.list_item_1, partyCodes);
                            pCodeTextView.setAdapter(partyCodeAdapter);
                        }
                    } else {
                        List<String> pCodes = candidateDbHelper.findPartyCodeByPartyName(autoCompleteTextViews.getPartyName());
                        if (pCodes.size() == 1) {
                            pCodeTextView.setText(pCodes.get(0));
                            String candidate = candidateDbHelper.findCandidateNamesByPartyCodeAndACCode(autoCompleteTextViews.getPartyCode(), autoCompleteTextViews.getACCode());
                            candidateNameTextView.setText(candidate);
                        } else {
                            AutoSuggestAdapter partyCodeAdapter = new AutoSuggestAdapter(this.getContext(), R.layout.list_item_1, pCodes);
                            pCodeTextView.setAdapter(partyCodeAdapter);
                            AutoSuggestAdapter candidateNameAdapter = new AutoSuggestAdapter(this.getContext(), R.layout.list_item_1, candidateDbHelper.findCandidateNameByPartyNameAndACCode(autoCompleteTextViews.getACCode(), autoCompleteTextViews.getPartyName()));
                            candidateNameTextView.setAdapter(candidateNameAdapter);
                        }

                    }
                }
            } else if (candidateNameTextView.isFocused() && autoCompleteTextViews.isValidCandidateName()) {
                List<String> partyNames = candidateDbHelper.findPartyNamesByCandidateName(autoCompleteTextViews.getCandidateName());
                List<String> partyCodes = candidateDbHelper.findPartyCodesByCandidateName(autoCompleteTextViews.getCandidateName());
                if (partyNames.size() == 1) {
                    pNameTextView.setText(partyNames.get(0));
                } else {
                    AutoSuggestAdapter partyNameAdapter = new AutoSuggestAdapter(this.getContext(), R.layout.list_item_1, partyNames);
                    pNameTextView.setAdapter(partyNameAdapter);
                }
                if (partyCodes.size() == 1) {
                    pCodeTextView.setText(partyCodes.get(0));
                } else {
                    AutoSuggestAdapter partyCodeAdapter = new AutoSuggestAdapter(this.getContext(), R.layout.list_item_1, partyCodes);
                    pCodeTextView.setAdapter(partyCodeAdapter);
                }
            } else {
                AutoSuggestAdapter partyNameAdapter = new AutoSuggestAdapter(this.getContext(), R.layout.list_item_1, candidateDbHelper.findPartyNamesByACCode(autoCompleteTextViews.getACCode()));
                pNameTextView.setAdapter(partyNameAdapter);
                AutoSuggestAdapter partyCodeAdapter = new AutoSuggestAdapter(this.getContext(), R.layout.list_item_1, candidateDbHelper.findPartyCodesByACCode(autoCompleteTextViews.getACCode()));
                pCodeTextView.setAdapter(partyCodeAdapter);
                AutoSuggestAdapter candidateNameAdapter = new AutoSuggestAdapter(this.getContext(), R.layout.list_item_1, candidateDbHelper.findCandidateNamesByACCode(autoCompleteTextViews.getACCode()));
                candidateNameTextView.setAdapter(candidateNameAdapter);
            }

        } else {
            AutoSuggestAdapter partyNameAdapter = new AutoSuggestAdapter(this.getContext(), R.layout.list_item_1, Arrays.asList("No Result"));
            pNameTextView.setAdapter(partyNameAdapter);
            AutoSuggestAdapter partyCodeAdapter = new AutoSuggestAdapter(this.getContext(), R.layout.list_item_1, Arrays.asList("No Result"));
            pCodeTextView.setAdapter(partyCodeAdapter);
            AutoSuggestAdapter candidateNameAdapter = new AutoSuggestAdapter(this.getContext(), R.layout.list_item_1, Arrays.asList("No Result"));
            candidateNameTextView.setAdapter(candidateNameAdapter);
        }
        if (electionCode != null && autoCompleteTextViews.isValidACText()) {
            getLastRoundDataList();
        }
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
                setUpAutoCompleteTextViews();
            }
        });
        acNameTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                acTextView.setText("");
                setUpAutoCompleteTextViews();
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
        mProgressDialog.setMessage("Refreshing constituency details...Please Wait...");
        mProgressDialog.show();
        NetworkRequest request = NetworkRequest.getInstance();
        request.doGetCandidates(token, doGetCandidatesCallback);
    }

    private void getLastRoundDataList() {
        NetworkRequest request = NetworkRequest.getInstance();
        request.doGetLastRoundData(token, electionCode, doGetLastRoundData);
    }


    public RoundUpdateBody prepareUpdate() {
        RoundUpdateBody roundUpdate = null;
        try {
            roundUpdate = new RoundUpdateBody();
            if (autoCompleteTextViews.isValidCandidateName() && autoCompleteTextViews.isValidACNameText() && autoCompleteTextViews.isValidPartyCode() && autoCompleteTextViews.isValidPartyName() && autoCompleteTextViews.isValidACText()) {
                roundUpdate.setAssemblyConstitutionCode(Integer.parseInt(autoCompleteTextViews.getACCode()));
                roundUpdate.setPartyCode(autoCompleteTextViews.getPartyCode());
                roundUpdate.setCandidateCode(candidateDbHelper.findCandidateCodeByACCodeAndCandidateName(autoCompleteTextViews.getACCode(), autoCompleteTextViews.getCandidateName()));
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
        if (autoCompleteTextViews.getRoundNo() <= getRoundNo()) {
            roundNoTextView.setError("Round no must be greater than " + getRoundNo());
            return;
        }
        mProgressDialog.setMessage("Updating Round...Please Wait...");
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        NetworkRequest request = NetworkRequest.getInstance();
        request.doUpdateRound(token, prepareUpdate(), dpPostRoundUpdate);
    }


    private NetworkRequest.Callback<Candidate[]> doGetCandidatesCallback = new NetworkRequest.Callback<Candidate[]>() {


        @Override
        public void onResponse(int responseCode, @NonNull Candidate[] response) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    candidateDbHelper.addCandidate(electionCode, response);
                    setUpViews();
                    autoCompleteTextViews = new AutoCompleteTextViews(acTextView, acNameTextView, roundNoTextView, pCodeTextView, pNameTextView, candidateNameTextView, candidateDbHelper);
                    setUpAutoCompleteTextViews();
                    makeToast("Success!!");
                    mProgressDialog.dismiss();
                }
            });
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
                makeToast("Not able to fetch latest election data..");
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
            }else{
                if(response != null && !response.isBlank()) {
                    makeToast(response);
                }else{
                    makeToast("Api returned empty response");
                }
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
}