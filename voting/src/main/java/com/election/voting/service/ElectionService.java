package com.election.voting.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.election.voting.response.ElectionUpdateResponse;
import com.election.voting.model.Admin;
import com.election.voting.model.Election;
import com.election.voting.response.ElectionResponse;
import com.election.voting.response.EligibilityResponse;


public interface ElectionService {
    List<Election> getAllElections();
    Long getElectionIdByName(String name);
    Election getElectionById(Long id);
    ElectionResponse createElection(Election election, Admin admin) throws Exception;
    public ElectionUpdateResponse updateElection(Long electionId, Election updatedElection, Admin admin);
    void deleteElection(Long id);
    
    void enableElection(Long id, LocalDateTime startTime, LocalDateTime endTime);
    void disableElection(Long id);

    public void setVotersForElection(Long electionId, Set<Long> voterIds);

    //-------------------------- check given voter is eligible for VIDHAN_SABHA election or not
    public EligibilityResponse checkVoterEligibility(String voterId);

    //-------------------------- check given voter is eligible for Grampanchyat election or not
    public EligibilityResponse checkVoterEligibilityNew(String voterId);

    //-------------------------- check given voter is eligible for LOK_SABHA election or not
    public EligibilityResponse checkVoterEligibilityForCentral(String voterId);

    //-------------------------- check given voter is eligible for NAGARSEVAK election or not
    public EligibilityResponse checkVoterEligibilityForNagarsevak(String voterId);

    //-------------------------- check given voter is eligible for NAGAR ADHYAKSHA election or not
    public EligibilityResponse checkVoterEligibilityForNagarAdhyaksha(String voterId);
}