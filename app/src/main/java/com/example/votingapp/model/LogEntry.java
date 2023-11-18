package com.example.votingapp.model;

public class LogEntry {
    private String acCode;
    private String acName;
    private String partyName;
    private String partyCode;
    private String candidateName;
    private int roundNo;

    private String userName;

    private String electionCode;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getElectionCode() {
        return electionCode;
    }

    public void setElectionCode(String electionCode) {
        this.electionCode = electionCode;
    }

    private String timeStamp;

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "LogEntry{" + "acCode='" + acCode + '\'' + ", acName='" + acName + '\'' + ", partyName='" + partyName + '\'' + ", partyCode='" + partyCode + '\'' + ", candidateName='" + candidateName + '\'' + ", roundNo=" + roundNo + '}';
    }

    public String toLog() {
        return String.format("AC Code: %s, AC Name: %s, \n PartyCode: %s, PartyName : %s, Candidate Name: %s \n Updated Round : %d", acCode, acName, partyCode, partyName, candidateName, roundNo);
    }

    public LogEntry(String acCode, String acName, String partyName, String partyCode, String candidateName, int roundNo, String userName, String electionCode) {
        this.acCode = acCode;
        this.acName = acName;
        this.partyName = partyName;
        this.partyCode = partyCode;
        this.candidateName = candidateName;
        this.roundNo = roundNo;
        this.userName = userName;
        this.electionCode = electionCode;
    }

    public LogEntry(String acCode, String acName, String partyName, String partyCode, String candidateName, int roundNo, String timeStamp) {
        this.acCode = acCode;
        this.acName = acName;
        this.partyName = partyName;
        this.partyCode = partyCode;
        this.candidateName = candidateName;
        this.roundNo = roundNo;
        this.timeStamp = timeStamp;
    }

    public String getAcCode() {
        return acCode;
    }

    public void setAcCode(String acCode) {
        this.acCode = acCode;
    }

    public String getAcName() {
        return acName;
    }

    public void setAcName(String acName) {
        this.acName = acName;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public String getPartyCode() {
        return partyCode;
    }

    public void setPartyCode(String partyCode) {
        this.partyCode = partyCode;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public int getRoundNo() {
        return roundNo;
    }

    public void setRoundNo(int roundNo) {
        this.roundNo = roundNo;
    }
}
