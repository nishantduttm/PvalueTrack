package com.example.votingapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AssemblyConstituency {

    Map<Integer, String> stateMap = new HashMap<Integer, String>() {{
        put(1, "Andhra Pradesh");
        put(2, "Arunachal Pradesh");
        put(3, "Assam");
        put(4, "Bihar");
        put(5, "Goa");
        put(6, "Gujarat");
        put(7, "Haryana");
        put(8, "Himachal Pradesh");
        put(9, "Jammu and Kashmir");
        put(10, "Karnataka");
        put(11, "Kerala");
        put(12, "Madhya Pradesh");
        put(13, "Maharashtra");
        put(14, "Manipur");
        put(15, "Meghalaya");
        put(16, "Mizoram");
        put(17, "Nagaland");
        put(18, "Odisha");
        put(19, "Punjab");
        put(20, "Rajasthan");
        put(21, "Sikkim");
        put(22, "Tamil Nadu");
        put(23, "Tripura");
        put(24, "Uttar Pradesh");
        put(25, "West Bengal");
        put(26, "Chhattisgarh");
        put(27, "Jharkhand");
        put(28, "Uttarakhand");
        put(29, "Telangana");
        put(35, "Delhi");
        put(37, "Puducherry");
        put(34, "Daman and Diu,Dadra and Nagar Haveli");
        put(31, "Andaman and Nicobar Islands");
        put(36, "Lakshadweep (ST)");
        put(33, "Ladakh");
    }};

    Set<String> acNameListWithNonUniqueNames = new HashSet<>(Arrays.asList("hamirpur", "aurangabad", "maharajganj"));

    @SerializedName("ac_Code")
    String acCode;
    @SerializedName("ac_Name")
    String acName;

    @SerializedName("ac_state")
    String stateCode;

    String stateName;

    String displayName;

    public String getDisplayName() {
        if(acNameListWithNonUniqueNames.contains(getAcName().toLowerCase())) {
            return getAcName() + " ( " +getStateName() + " )";
        }else{
            return getAcName();
        }
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public AssemblyConstituency() {
    }

    @Override
    public String toString() {
        return "AssemblyConstituency{" +
                "acCode='" + acCode + '\'' +
                ", acName='" + acName + '\'' +
                ", stateCode='" + stateCode + '\'' +
                ", stateName='" + stateName + '\'' +
                ", displayName='" + displayName + '\'' +
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


    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getStateName() {
        return stateMap.get(Integer.parseInt(stateCode));
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}
