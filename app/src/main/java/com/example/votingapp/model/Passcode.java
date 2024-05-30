package com.example.votingapp.model;

import com.google.gson.annotations.SerializedName;

public class Passcode {
    String passcode;
    @SerializedName("electioncode")
    String electionCode;

    @SerializedName("etype")
    Integer electionType;

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public Integer getElectionType() {
        return electionType;
    }

    public void setElectionType(Integer electionType) {
        this.electionType = electionType;
    }

    @Override
    public String toString() {
        return "Passcode{" +
                "passcode='" + passcode + '\'' +
                ", electionCode='" + electionCode + '\'' +
                '}';
    }

    public String getElectionCode() {
        return electionCode;
    }

    public void setElectionCode(String electionCode) {
        this.electionCode = electionCode;
    }
}
