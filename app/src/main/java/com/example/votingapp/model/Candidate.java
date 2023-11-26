package com.example.votingapp.model;


import com.example.votingapp.ApiLib.NetworkRequest;
import com.google.gson.annotations.SerializedName;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Candidate implements NetworkRequest.ApiResponse {
    @SerializedName("pcode")
    String partyCode;
    @SerializedName("pname")
    String partyName;
    @SerializedName("candiname")
    String candidateName;
    @SerializedName("candicode")
    int candidateCode;


    public Candidate() {
    }


    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
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

    public int getCandidateCode() {
        return candidateCode;
    }

    public void setCandidateCode(int candidateCode) {
        this.candidateCode = candidateCode;
    }



    @Override
    public String toString() {
        return "Candidate{" +
                "partyCode='" + partyCode + '\'' +
                ", partyName='" + partyName + '\'' +
                ", candidateName='" + candidateName + '\'' +
                ", candidateCode=" + candidateCode +
                '}';
    }

    @Override
    public String string() {
        return this.toString();
    }
}
