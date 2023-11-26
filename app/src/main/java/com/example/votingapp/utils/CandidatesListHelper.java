package com.example.votingapp.utils;

import com.example.votingapp.model.Candidate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CandidatesListHelper {
    Candidate[] candidateList;

    public CandidatesListHelper(Candidate[] candidateList) {
        this.candidateList = candidateList;
    }

    public List<String> getPartCodes(){
        Set<String> partyCodes = new HashSet<>();
        for(Candidate candidate : candidateList) {
            partyCodes.add(candidate.getPartyCode());
        }
        return new ArrayList<>(partyCodes);
    }

    public List<String> getCandidateNames(){
        Set<String> candidateNames = new HashSet<>();
        for(Candidate candidate : candidateList){
            candidateNames.add(candidate.getCandidateName());
        }
        return new ArrayList<>(candidateNames);
    }

    public List<String> getPartyNames(){
        Set<String> partyNames = new HashSet<>();
        for(Candidate candidate : candidateList){
            partyNames.add(candidate.getPartyName());
        }
        return new ArrayList<>(partyNames);
    }

    public String findPartyNameByPartyCode(String partyCode){
        for(Candidate candidate : candidateList){
            if(candidate.getPartyCode().equals(partyCode)){
                return candidate.getPartyName();
            }
        }
        return "";
    }

    public String findCandidateNamesByPartyCode(String partyCode){
        for(Candidate candidate : candidateList){
            if(candidate.getPartyCode().equals(partyCode)) {
                return candidate.getCandidateName();
            }
        }
        return "";
    }

    public List<String> findPartyCodeByPartyName(String partyName){
        Set<String> partyCodes = new HashSet<>();
        for(Candidate candidate : candidateList){
            if(candidate.getPartyName().equals(partyName)){
                partyCodes.add(candidate.getPartyCode());
            }
        }
        return new ArrayList<>(partyCodes);
    }

    public List<String> findPartyCodesByCandidateNameAndPartyName(String candidateName, String partyName){
        Set<String> partyCodes = new HashSet<>();
        for(Candidate candidate : candidateList){
            if(candidate.getPartyName().equals(partyName) && candidate.getCandidateName().equals(candidateName)) {
                partyCodes.add(candidate.getPartyCode());
            }
        }
        return new ArrayList<>(partyCodes);
    }

    public List<String> findCandidateNamesByPartyName(String partyName){
        Set<String> candidateNames = new HashSet<>();
        for(Candidate candidate : candidateList){
            if(candidate.getPartyName().equals(partyName)) {
                candidateNames.add(candidate.getCandidateName());
            }
        }
        return new ArrayList<>(candidateNames);
    }

    public List<String> findPartyCodesByCandidateName(String candidateName){
        Set<String> partyCodes = new HashSet<>();
        for(Candidate candidate : candidateList){
            if(candidate.getCandidateName().equals(candidateName)) {
                partyCodes.add(candidate.getPartyCode());
            }
        }
        return new ArrayList<>(partyCodes);
    }

    public List<String> findPartyNamesByCandidateName(String candidateName){
        Set<String> partyNames = new HashSet<>();
        for(Candidate candidate : candidateList){
            if(candidate.getCandidateName().equals(candidateName)) {
                partyNames.add(candidate.getPartyName());
            }
        }
        return new ArrayList<>(partyNames);
    }

    public int findCandidateCodeByCandidateNameAndPartyCode(String candidateName, String partyCode){
        for(Candidate candidate : candidateList){
            if(candidate.getCandidateName().equals(candidateName) && candidate.getPartyCode().equals(partyCode)) {
                return candidate.getCandidateCode();
            }
        }
        return 0;
    }

    public boolean isValidPartyCode(String partyCode){
        for(Candidate candidate : candidateList){
            if(candidate.getPartyCode().equals(partyCode)){
                return true;
            }
        }
        return false;
    }

    public boolean isValidPartyName(String partyName){
        for(Candidate candidate : candidateList){
            if(candidate.getPartyName().equals(partyName)){
                return true;
            }
        }
        return false;
    }

    public boolean isValidCandidateName(String candidateName){
        for(Candidate candidate : candidateList){
            if(candidate.getCandidateName().equals(candidateName)){
                return true;
            }
        }
        return false;
    }


}
