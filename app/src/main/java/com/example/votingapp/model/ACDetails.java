package com.example.votingapp.model;

import com.google.gson.annotations.SerializedName;

public class ACDetails {
    @SerializedName("pcode")
    String partyCode;
    @SerializedName("pname")
    String partyName;
    @SerializedName("candicode")
    String candidateCode;
    @SerializedName("candiname")
    String candidateName;

    public ACDetails() {
    }

    public String getPartyCode() {
        return partyCode;
    }

    public void setPartyCode(String partyCode) {
        this.partyCode = partyCode;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public String getCandidateCode() {
        return candidateCode;
    }

    public void setCandidateCode(String candidateCode) {
        this.candidateCode = candidateCode;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    @Override
    public String toString() {
        return "ACDetails{" +
                "partyCode='" + partyCode + '\'' +
                ", partyName='" + partyName + '\'' +
                ", candidateCode='" + candidateCode + '\'' +
                ", candidateName='" + candidateName + '\'' +
                '}';
    }
}
