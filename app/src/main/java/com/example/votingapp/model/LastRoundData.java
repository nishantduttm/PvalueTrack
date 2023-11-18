package com.example.votingapp.model;

public class LastRoundData {

    private String ecode;
    private String accode;
    private int round;
    private String acname;
    private String pcode;
    private String pname;
    private String pabb;
    private String candicode;
    private String candiname;
    private String statecode;

    @Override
    public String toString() {
        return "LastRoundData{" +
                "ecode='" + ecode + '\'' +
                ", accode='" + accode + '\'' +
                ", round=" + round +
                ", acname='" + acname + '\'' +
                ", pcode='" + pcode + '\'' +
                ", pname='" + pname + '\'' +
                ", pabb='" + pabb + '\'' +
                ", candicode='" + candicode + '\'' +
                ", candiname='" + candiname + '\'' +
                ", statecode='" + statecode + '\'' +
                '}';
    }

    public String getEcode() {
        return ecode;
    }

    public void setEcode(String ecode) {
        this.ecode = ecode;
    }

    public String getAccode() {
        return accode;
    }

    public void setAccode(String accode) {
        this.accode = accode;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public String getAcname() {
        return acname;
    }

    public void setAcname(String acname) {
        this.acname = acname;
    }

    public String getPcode() {
        return pcode;
    }

    public void setPcode(String pcode) {
        this.pcode = pcode;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPabb() {
        return pabb;
    }

    public void setPabb(String pabb) {
        this.pabb = pabb;
    }

    public String getCandicode() {
        return candicode;
    }

    public void setCandicode(String candicode) {
        this.candicode = candicode;
    }

    public String getCandiname() {
        return candiname;
    }

    public void setCandiname(String candiname) {
        this.candiname = candiname;
    }

    public String getStatecode() {
        return statecode;
    }

    public void setStatecode(String statecode) {
        this.statecode = statecode;
    }
}
