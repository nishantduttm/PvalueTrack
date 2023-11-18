package com.example.votingapp.model;


import com.example.votingapp.ApiLib.NetworkRequest;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Candidate implements NetworkRequest.ApiResponse {
    String assemblyConstituencyCode;
    String assemblyConstituencyName;
    String partyCode;
    String partyName;

    String candidateName;
    int candidateCode;

    String electionCode;
    String electionName;

    public Candidate() {
    }

    public String getElectionName() {
        return electionName;
    }

    public void setElectionName(String electionName) {
        this.electionName = electionName;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    String stateCode;

    public String getAssemblyConstituencyCode() {
        return assemblyConstituencyCode;
    }

    public void setAssemblyConstituencyCode(String assemblyConstituencyCode) {
        this.assemblyConstituencyCode = assemblyConstituencyCode;
    }

    public String getAssemblyConstituencyName() {
        return assemblyConstituencyName;
    }

    public void setAssemblyConstituencyName(String assemblyConstituencyName) {
        this.assemblyConstituencyName = assemblyConstituencyName;
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

    public String getElectionCode() {
        return electionCode;
    }

    public void setElectionCode(String electionCode) {
        this.electionCode = electionCode;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "assemblyConstituencyCode='" + assemblyConstituencyCode + '\'' +
                ", assemblyConstituencyName='" + assemblyConstituencyName + '\'' +
                ", partyCode='" + partyCode + '\'' +
                ", partyName='" + partyName + '\'' +
                ", candidateCode=" + candidateCode +
                ", electionCode='" + electionCode + '\'' +
                ", stateCode='" + stateCode + '\'' +
                '}';
    }

    @Override
    public String string() {
        return this.toString();
    }
}
