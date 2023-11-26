package com.example.votingapp.UIHelper;

import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.example.votingapp.utils.ACHelper;
import com.example.votingapp.utils.CandidatesListHelper;

public class AutoCompleteTextViews {
    AutoCompleteTextView acTextView;
    AutoCompleteTextView acNameTextView;
    EditText roundNoTextView;
    AutoCompleteTextView pCodeTextView;
    AutoCompleteTextView pNameTextView;
    AutoCompleteTextView candidateNameTextView;

    public AutoCompleteTextViews(AutoCompleteTextView acTextView, AutoCompleteTextView acNameTextView, EditText roundNoTextView, AutoCompleteTextView pCodeTextView, AutoCompleteTextView pNameTextView, AutoCompleteTextView candidateNameTextView, CandidatesListHelper candidateHelper, ACHelper acHelper) {
        this.acTextView = acTextView;
        this.acNameTextView = acNameTextView;
        this.roundNoTextView = roundNoTextView;
        this.pCodeTextView = pCodeTextView;
        this.pNameTextView = pNameTextView;
        this.candidateNameTextView = candidateNameTextView;
        this.candidatesListHelper = candidateHelper;
        this.acHelper = acHelper;
    }

    CandidatesListHelper candidatesListHelper;

    ACHelper acHelper;


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
        if(candidatesListHelper == null){
            return false;
        }
        return candidatesListHelper.isValidCandidateName(candidateName);
    }

    public boolean isValidPartyName() {
        String partyName = getPartyName();
        if(candidatesListHelper == null){
            return false;
        }
        return candidatesListHelper.isValidPartyName(partyName);
    }

    public boolean isValidPartyCode() {
        String partyCode = getPartyCode();
        if(candidatesListHelper == null){
            return false;
        }
        return candidatesListHelper.isValidPartyCode(partyCode);
    }

    public boolean isValidACText() {
        String acCode = getACCode();
        Log.d("info", "isValidACText: " + acCode);
        if(acHelper == null){
            return false;
        }
        return acHelper.isValidACCode(acCode);
    }


    public boolean isValidACNameText() {
        String acName = getACName();
        if(acHelper == null){
            return false;
        }
        return acHelper.isValidACName(acName);
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
