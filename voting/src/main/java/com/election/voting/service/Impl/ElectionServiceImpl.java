package com.election.voting.service.Impl;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.election.voting.model.Admin;
import com.election.voting.model.AdminRole;
import com.election.voting.model.Election;
import com.election.voting.model.ElectionAssembly;
import com.election.voting.model.Voter;
import com.election.voting.repository.ElectionRepository;
import com.election.voting.repository.VoterRepository;
import com.election.voting.response.ApiResponse;
import com.election.voting.response.ElectionResponse;
import com.election.voting.response.ElectionUpdateResponse;
import com.election.voting.response.EligibilityResponse;
import com.election.voting.service.ElectionService;

import jakarta.transaction.Transactional;

@Service
public class ElectionServiceImpl implements ElectionService {

    @Autowired
    ElectionRepository electionRepository;

    @Autowired
    VoterRepository voterRepository;

    @Override
    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }

    @Override
    public Long getElectionIdByName(String name) {
        return electionRepository.findIdByName(name);
    }

    @Override
    public Election getElectionById(Long id) {
        return electionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Election not found"));
    }

    @Override
    public ElectionResponse createElection(Election election, Admin admin) {
        try {
            if (admin.getRole() == AdminRole.CITY_NAGAR_ADHYAKSHA) {
                election.setAssembly(ElectionAssembly.NAGARADHYAKSHA);
            } else if (admin.getRole() == AdminRole.CITY_NAGAR_SEVAK) {
                election.setAssembly(ElectionAssembly.NAGARSEVAK);
            } else if (admin.getRole() == AdminRole.VILLAGE) {
                election.setAssembly(ElectionAssembly.GRAM_PANCHAYAT);
            }

            election.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            Election createdElection = electionRepository.save(election);

            return new ElectionResponse(createdElection, "Election created successfully");
        } catch (Exception e) {
            return new ElectionResponse(null, "Failed to create election");
        }
    }

    @Override
    public ElectionUpdateResponse updateElection(Long electionId, Election updatedElection, Admin admin) {
        try {
            Optional<Election> existingElection = electionRepository.findById(electionId);
            if (existingElection.isPresent()) {
                Election currentElection = existingElection.get();

                // Check if the admin has access to update this election
                if (admin.getRole() == AdminRole.valueOf(currentElection.getName())) {
                    // Use reflection to update non-null fields of the current election with the new
                    // values
                    Field[] fields = Election.class.getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        Object updatedValue = field.get(updatedElection);
                        if (updatedValue != null) {
                            field.set(currentElection, updatedValue);
                        }
                    }

                    currentElection.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

                    Election updated = electionRepository.save(currentElection);
                    return new ElectionUpdateResponse(updated, "Election updated successfully");
                } else {
                    return new ElectionUpdateResponse(null, "You don't have access to update this election");
                }
            } else {
                return new ElectionUpdateResponse(null, "Election not found");
            }
        } catch (Exception e) {
            return new ElectionUpdateResponse(null, "Failed to update election");
        }
    }

    @Override
    public void deleteElection(Long id) {
        if (!electionRepository.existsById(id)) {
            throw new NoSuchElementException("Election not found");
        }
        electionRepository.deleteById(id);
    }

    @Override
    public void enableElection(Long id, LocalDateTime startTime, LocalDateTime endTime) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Election not found"));
        election.setStartTime(startTime);
        election.setEndTime(endTime);
        electionRepository.save(election);
    }

    @Override
    public void disableElection(Long id) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Election not found"));
        election.setStartTime(null);
        election.setEndTime(null);
        electionRepository.save(election);
    }

    @Transactional
    @Override
    public void setVotersForElection(Long electionId, Set<Long> voterIds) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new IllegalArgumentException("Election not found"));

        // Retrieve the Voter entities
        Set<Voter> voters = (Set<Voter>) voterRepository.findAllById(voterIds);
        System.out.println(voters);

        // Update the voters set of the Election entity
        election.setVoters(voters);

        // Save the Election entity
        electionRepository.save(election);
    }

    // -------------------------- check given voter is eligible for VIDHAN_SABHA
    // election or not
    @Override
    public EligibilityResponse checkVoterEligibility(String voterId) {
        // Find the voter by voterId
        System.out.println("--------------- election servive implementation ------");
        Voter voter = voterRepository.findByVoterId(voterId);
        System.out.println("------- voter = " + voter);
        if (voter == null) {
            System.out.println("voter is null");
            throw new NoSuchElementException("Voter not found");
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        List<Election> eligibleElections = electionRepository.findEligibleElections(currentDateTime, voter.getState());
        System.out.println("------------ list eligibleElctions  = " + eligibleElections);
        System.out.println("------------ size of list = " + eligibleElections.size());

        // Check the assembly type and state for eligibility
        List<Long> eligibleElectionIds = eligibleElections.stream()
                .filter(election -> election.getAssembly() == ElectionAssembly.VIDHAN_SABHA
                        && election.getLocation().equals(voter.getState()))
                .map(Election::getId) // Assuming getId() returns the election ID
                .collect(Collectors.toList());

        System.out.println("---------------- eligbible Elections Ids = " + eligibleElectionIds);
        System.out.println("---------------- size of eligbible elctions ids = " + eligibleElectionIds.size());
        ApiResponse res = new ApiResponse();
        res.setMessage("voter is eligible");
        boolean eligible = !eligibleElectionIds.isEmpty();
        System.out.println("--------------- eligible = " + eligible);

        EligibilityResponse response = new EligibilityResponse();
        response.setEligible(eligible);
        response.setMessage("voter is eligible");
        response.setVoterId(voterId);
        response.setEligibleElectionIds(eligibleElectionIds);

        System.out.println("--------------- eligible = " + eligible);
        return response;
        // return new EligibilityResponse(eligible, eligibleElectionIds);
    }

    // -------------------------- check given voter is eligible for Grampanchyat
    // election or not
    @Override
    public EligibilityResponse checkVoterEligibilityNew(String voterId) {
        // Find the voter by voterId
        System.out.println("--------------- election servive implementation ------");
        Voter voter = voterRepository.findByVoterId(voterId);
        System.out.println("------- voter = " + voter);
        if (voter == null) {
            System.out.println("voter is null");
            throw new NoSuchElementException("Voter not found");
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        List<Election> eligibleElections = electionRepository.findEligibleElectionsNew(currentDateTime,
                voter.getCityOrVillage());
        System.out.println("------------ list eligibleElctions  = " + eligibleElections);
        System.out.println("------------ size of list = " + eligibleElections.size());

        // Check the assembly type and state for eligibility
        List<Long> eligibleElectionIds = eligibleElections.stream()
                .filter(election -> election.getAssembly() == ElectionAssembly.GRAM_PANCHAYAT
                        && election.getLocation().equals(voter.getCityOrVillage()))
                .map(Election::getId) // Assuming getId() returns the election ID
                .collect(Collectors.toList());

        System.out.println("---------------- eligbible Elections Ids = " + eligibleElectionIds);
        System.out.println("---------------- size of eligbible elctions ids = " + eligibleElectionIds.size());
        ApiResponse res = new ApiResponse();
        res.setMessage("voter is eligible");
        boolean eligible = !eligibleElectionIds.isEmpty();
        System.out.println("--------------- eligible = " + eligible);

        EligibilityResponse response = new EligibilityResponse();
        response.setEligible(eligible);
        response.setMessage("voter is eligible");
        response.setVoterId(voterId);
        response.setEligibleElectionIds(eligibleElectionIds);

        System.out.println("--------------- eligible = " + eligible);
        return response;

        // return new EligibilityResponse(eligible, eligibleElectionIds);
    }

    // -------------------------- check given voter is eligible for LOK_SABHA
    // election or not
    @Override
    public EligibilityResponse checkVoterEligibilityForCentral(String voterId) {
        // Find the voter by voterId
        System.out.println("--------------- election servive implementation ------");
        Voter voter = voterRepository.findByVoterId(voterId);
        System.out.println("------- voter = " + voter);
        if (voter == null) {
            System.out.println("voter is null");
            throw new NoSuchElementException("Voter not found");
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        List<Election> eligibleElections = electionRepository.findEligibleElectionsForCentral(currentDateTime,
                voter.getCountry());
        System.out.println("------------ list eligibleElctions  = " + eligibleElections);
        System.out.println("------------ size of list = " + eligibleElections.size());

        // Check the assembly type and state for eligibility
        List<Long> eligibleElectionIds = eligibleElections.stream()
                .filter(election -> election.getAssembly() == ElectionAssembly.LOK_SABHA
                        && election.getLocation().equals(voter.getCountry()))
                .map(Election::getId) // Assuming getId() returns the election ID
                .collect(Collectors.toList());

        System.out.println("---------------- eligbible Elections Ids = " + eligibleElectionIds);
        System.out.println("---------------- size of eligbible elctions ids = " + eligibleElectionIds.size());
        boolean eligible = !eligibleElectionIds.isEmpty();
        System.out.println("--------------- eligible = " + eligible);

        EligibilityResponse response = new EligibilityResponse();
        response.setEligible(eligible);
        response.setMessage("voter is eligible");
        response.setVoterId(voterId);
        response.setEligibleElectionIds(eligibleElectionIds);

        System.out.println("--------------- eligible = " + eligible);
        return response;

        // return new EligibilityResponse(eligible, eligibleElectionIds);
    }

    // -------------------------- check given voter is eligible for NAGARSEVAK
    // election or not
    @Override
    public EligibilityResponse checkVoterEligibilityForNagarsevak(String voterId) {
        // Find the voter by voterId
        System.out.println("--------------- election servive implementation ------");
        Voter voter = voterRepository.findByVoterId(voterId);
        System.out.println("------- voter = " + voter);
        if (voter == null) {
            System.out.println("voter is null");
            throw new NoSuchElementException("Voter not found");
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        List<Election> eligibleElections = electionRepository.findEligibleElectionsNew(currentDateTime,
                voter.getCityOrVillage());
        System.out.println("------------ list eligibleElctions  = " + eligibleElections);
        System.out.println("------------ size of list = " + eligibleElections.size());

        // Check the assembly type and state for eligibility
        List<Long> eligibleElectionIds = eligibleElections.stream()
                .filter(election -> election.getAssembly() == ElectionAssembly.NAGARSEVAK
                        && election.getLocation().equals(voter.getCityOrVillage()))
                .map(Election::getId) // Assuming getId() returns the election ID
                .collect(Collectors.toList());

        System.out.println("---------------- eligbible Elections Ids = " + eligibleElectionIds);
        System.out.println("---------------- size of eligbible elctions ids = " + eligibleElectionIds.size());
        boolean eligible = !eligibleElectionIds.isEmpty();
        System.out.println("--------------- eligible = " + eligible);

        EligibilityResponse response = new EligibilityResponse();
        response.setEligible(eligible);
        response.setMessage("voter is eligible");
        response.setVoterId(voterId);
        response.setEligibleElectionIds(eligibleElectionIds);

        System.out.println("--------------- eligible = " + eligible);
        return response;

        // return new EligibilityResponse(eligible, eligibleElectionIds);
    }

    // -------------------------- check given voter is eligible for NAGAR ADHYAKSHA
    // election or not
    @Override
    public EligibilityResponse checkVoterEligibilityForNagarAdhyaksha(String voterId) {
        // Find the voter by voterId
        System.out.println("--------------- election servive implementation ------");
        Voter voter = voterRepository.findByVoterId(voterId);
        System.out.println("------- voter = " + voter);
        if (voter == null) {
            System.out.println("voter is null");
            throw new NoSuchElementException("Voter not found");
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        List<Election> eligibleElections = electionRepository.findEligibleElectionsNew(currentDateTime,
                voter.getCityOrVillage());
        System.out.println("------------ list eligibleElctions  = " + eligibleElections);
        System.out.println("------------ size of list = " + eligibleElections.size());

        // Check the assembly type and state for eligibility
        List<Long> eligibleElectionIds = eligibleElections.stream()
                .filter(election -> election.getAssembly() == ElectionAssembly.NAGARADHYAKSHA
                        && election.getLocation().equals(voter.getCityOrVillage()))
                .map(Election::getId) // Assuming getId() returns the election ID
                .collect(Collectors.toList());

        System.out.println("---------------- eligbible Elections Ids = " + eligibleElectionIds);
        System.out.println("---------------- size of eligbible elctions ids = " + eligibleElectionIds.size());
        boolean eligible = !eligibleElectionIds.isEmpty();
        System.out.println("--------------- eligible = " + eligible);

        EligibilityResponse response = new EligibilityResponse();
        response.setEligible(eligible);
        response.setMessage("voter is eligible");
        response.setVoterId(voterId);
        response.setEligibleElectionIds(eligibleElectionIds);

        System.out.println("--------------- eligible = " + eligible);
        return response;

        // return new EligibilityResponse(eligible, eligibleElectionIds);
    }
}