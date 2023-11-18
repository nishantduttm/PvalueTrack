package com.example.votingapp.UIHelper;

import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.example.votingapp.db.CandidateDbHelper;

public class AutoCompleteTextViews {
    AutoCompleteTextView acTextView;
    AutoCompleteTextView acNameTextView;
    EditText roundNoTextView;
    AutoCompleteTextView pCodeTextView;
    AutoCompleteTextView pNameTextView;
    AutoCompleteTextView candidateNameTextView;

    public AutoCompleteTextViews(AutoCompleteTextView acTextView, AutoCompleteTextView acNameTextView, EditText roundNoTextView, AutoCompleteTextView pCodeTextView, AutoCompleteTextView pNameTextView, AutoCompleteTextView candidateNameTextView, CandidateDbHelper candidateHelper) {
        this.acTextView = acTextView;
        this.acNameTextView = acNameTextView;
        this.roundNoTextView = roundNoTextView;
        this.pCodeTextView = pCodeTextView;
        this.pNameTextView = pNameTextView;
        this.candidateNameTextView = candidateNameTextView;
        this.candidateDbHelper = candidateHelper;
    }

    CandidateDbHelper candidateDbHelper;


    public String getACCode() {
        return acTextView.getText().toString().trim();
    }

    public String getACName() {
        return acNameTextView.getText().toString().trim();
    }

    public int getRoundNo() {
        try {
            return Integer.parseInt(roundNoTextView.getText().toString().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String getCandidateName() {
        return candidateNameTextView.getText().toString().trim();
    }

    public String getPartyName() {
        return pNameTextView.getText().toString().trim();
    }

    public String getPartyCode() {
        return pCodeTextView.getText().toString().trim();
    }

    public boolean isValidCandidateName() {
        String candidateName = getCandidateName();
        return candidateDbHelper.isValidCandidateName(candidateName);
    }

    public boolean isValidPartyName() {
        String partyName = getPartyName();
        return candidateDbHelper.isValidPartyName(partyName);
    }

    public boolean isValidPartyCode() {
        String partyCode = getPartyCode();
        return candidateDbHelper.isValidPartyCode(partyCode);
    }

    public boolean isValidACText() {
        String acCode = getACCode();
        Log.d("info", "isValidACText: " + acCode);
        if (candidateDbHelper.isValidACCode(acCode)) {
            return true;
        } else {
            return false;
        }
    }


    public boolean isValidACNameText() {
        String acName = getACName();
        return candidateDbHelper.isValidACName(acName);
    }

    public void setACText(String acCode){
        acTextView.setText(acCode);
    }

    public void setACNameText(String acName){
        acNameTextView.setText(acName);
    }

    public void setPartyNameText(String pName){
        pNameTextView.setText(pName);
    }

    public void setCandidateNameText(String candidateName){
        candidateNameTextView.setText(candidateName);
    }
}
