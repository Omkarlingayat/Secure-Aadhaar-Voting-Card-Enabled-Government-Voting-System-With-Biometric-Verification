package com.election.voting.service;

import java.util.List;

import com.election.voting.model.Admin;
import com.election.voting.model.Candidate;

public interface CandidateService {
    List<Candidate> getAllCandidates();

    Candidate getCandidateById(Long id);

    Candidate createCandidate(Admin admin, Candidate candidate);

    Candidate updateCandidate(String name, Candidate candidate, Admin admin) throws Exception;

    void deleteCandidate(Admin admin, Candidate candidate);


}
