package com.election.voting.service;

import java.util.List;
import java.util.Set;

import com.election.voting.model.Voter;

public interface VoterService {
    List<Voter> getAllVoters();
    Voter getVoterByAadhaarNumber(String aadhaarNumber);
    Voter getVoterByVoterId(String voterId);
    Voter createVoter(Voter voter);
    Voter updateVoter(Long id, Voter voter);
    void deleteVoter(Long id);
    public void setElectionsForVoter(String voterId, List<Long> electionIdList);
}