package com.election.voting.service;

import java.io.FileNotFoundException;
import java.util.Map;

import com.election.voting.model.Candidate;

import java.io.IOException;

public interface VoteService {

    public void castVote(String voterId,String aadhaarNumber, Long candidateId, Long electionId);
    
    Map<Candidate, Long> getElectionResults(Long electionId) throws IOException, FileNotFoundException;

    // public Map<Candidate, Long> generateElectionResultsPdf(Long electionId) throws IOException, java.io.IOException, FileNotFoundException;
}
