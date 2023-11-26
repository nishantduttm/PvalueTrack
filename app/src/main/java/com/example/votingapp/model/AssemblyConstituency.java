package com.example.votingapp.model;

import com.google.gson.annotations.SerializedName;

public class AssemblyConstituency {
    @SerializedName("ac_Code")
    String acCode;
    @SerializedName("ac_Name")
    String acName;

    public AssemblyConstituency() {
    }

    @Override
    public String toString() {
        return "AssemblyConstituency{" +
                "acCode='" + acCode + '\'' +
                ", acName='" + acName + '\'' +
                '}';
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
}
