package com.example.votingapp.model;

public class RoundUpdateBody {
    private int assemblyConstituencyCode;
    private int  round;
    private String partyCode;
    private int candidateCode;
    private String electionCode;

    public RoundUpdateBody() {
    }

    public RoundUpdateBody(int assemblyConstitutionCode, int round, String partyCode, int candidateCode, String electionCode) {
        this.assemblyConstituencyCode = assemblyConstitutionCode;
        this.round = round;
        this.partyCode = partyCode;
        this.candidateCode = candidateCode;
        this.electionCode = electionCode;
    }

    public int getAssemblyConstitutionCode() {
        return assemblyConstituencyCode;
    }

    public void setAssemblyConstitutionCode(int assemblyConstitutionCode) {
        this.assemblyConstituencyCode = assemblyConstitutionCode;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public String getPartyCode() {
        return partyCode;
    }

    public void setPartyCode(String partyCode) {
        this.partyCode = partyCode;
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
}
