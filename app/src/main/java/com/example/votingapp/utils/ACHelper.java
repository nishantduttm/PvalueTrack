package com.example.votingapp.utils;

import com.example.votingapp.model.AssemblyConstituency;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ACHelper {
    AssemblyConstituency[] assemblyConstituencies;

    public ACHelper(AssemblyConstituency[] assemblyConstituencies) {
        this.assemblyConstituencies = assemblyConstituencies;
    }

    public boolean isValidACCode(String acCode){
        for(AssemblyConstituency assemblyConstituency : assemblyConstituencies){
            if(assemblyConstituency.getAcCode().equals(acCode)){
                return true;
            }
        }
        return false;
    }

    public boolean isValidACName(String acName){
        for(AssemblyConstituency assemblyConstituency : assemblyConstituencies){
            if(assemblyConstituency.getDisplayName().equals(acName)){
                return true;
            }
        }
        return false;
    }

    public List<String> getACodeList(){
        Set<String> acCodeList = new HashSet<>();
        for(AssemblyConstituency assemblyConstituency : assemblyConstituencies){
            acCodeList.add(assemblyConstituency.getAcCode());
        }
        return new ArrayList<>(acCodeList).stream().sorted().collect(Collectors.toList());
    }

    public List<String> getACNameList(){
        Set<String> acNameList = new HashSet<>();
        for(AssemblyConstituency assemblyConstituency : assemblyConstituencies){
            acNameList.add(assemblyConstituency.getDisplayName());
        }
        return new ArrayList<>(acNameList);
    }

    public String findACCodeByACName(String acName){
        for(AssemblyConstituency assemblyConstituency : assemblyConstituencies){
            if(assemblyConstituency.getDisplayName().equals(acName)){
                return assemblyConstituency.getAcCode();
            }
        }
        return "";
    }

    public String findACNameByACCode(String acCode){
        for(AssemblyConstituency assemblyConstituency : assemblyConstituencies){
            if(assemblyConstituency.getAcCode().equals(acCode)){
                return assemblyConstituency.getDisplayName();
            }
        }
        return "";
    }



}
