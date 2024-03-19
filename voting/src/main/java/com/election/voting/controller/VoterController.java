package com.election.voting.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.election.voting.model.Candidate;
import com.election.voting.model.Election;
import com.election.voting.model.Voter;
import com.election.voting.repository.CandidateRepository;
import com.election.voting.repository.ElectionRepository;
import com.election.voting.repository.VoterRepository;
import com.election.voting.response.ApiResponse;
import com.election.voting.response.EligibilityResponse;
import com.election.voting.service.CandidateService;
import com.election.voting.service.ElectionService;
import com.election.voting.service.VoteService;
import com.election.voting.service.VoterService;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/voter")
public class VoterController {

    @Autowired
    private VoterService voterService;

    @Autowired
    private VoteService voteService;

    @Autowired
    CandidateService candidateService;

    @Autowired
    ElectionService electionService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VoterRepository voterRepository;

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    // Step 1: Verify voter by Aadhaar or Voter ID
    @GetMapping("/verify")
    public ResponseEntity<String> verifyVoter(@RequestParam(value = "voterId", required = false) String voterId,
            @RequestParam(value = "aadhaarNumber", required = false) String aadhaarNumber) {
    
        // Check if Aadhaar number is provided and not empty
        if (aadhaarNumber != null && !aadhaarNumber.isEmpty()) {
            // Retrieve voter information by Aadhaar number
            Voter voterByAadhaar = voterService.getVoterByAadhaarNumber(aadhaarNumber);
    
            // If the voter with the provided Aadhaar number is found
            if (voterByAadhaar != null) {
                    return new ResponseEntity<>("Voter verified by Aadhaar number", HttpStatus.OK);
         
            } else {
                return new ResponseEntity<>("Aadhaar number not found", HttpStatus.NOT_FOUND);
            }
        }
    
        // If Aadhaar number is not provided or not found, and Voter ID is provided
        if (voterId != null && !voterId.isEmpty()) {
            // Retrieve voter information by Voter ID
            Voter voterByVoterId = voterService.getVoterByVoterId(voterId);
    
            // If the voter with the provided Voter ID is not found
            if (voterByVoterId == null) {
                return new ResponseEntity<>("Voter not found", HttpStatus.NOT_FOUND);
            }
            
            // Voter verified by Voter ID
            return new ResponseEntity<>("Voter verified by Voter ID", HttpStatus.OK);
        }
    
        // If neither Aadhaar number nor Voter ID is provided
        return new ResponseEntity<>("Neither Aadhaar number nor Voter ID provided", HttpStatus.BAD_REQUEST);
    }
    
    // Step 2: Verify fingerprint (Assuming this will be implemented separately)

    // Step 3: Choose candidate/party and cast vote
    @PostMapping("/cast-vote")
    public ResponseEntity<String> castVote(@RequestBody Map<String, String> requestBody) {
        try {
            String voterId = requestBody.get("voterId");
            String aadhaarNumber = requestBody.get("aadhaarNumber");
            String candidateName = requestBody.get("candidateName");
            String electionName = requestBody.get("electionName");
            System.out.println(aadhaarNumber);
            System.out.println(voterId);
            System.out.println(candidateName);
            System.out.println(electionName);
            
            // Fetch candidateId and electionId based on candidateName and electionName
            Long candidateId = candidateRepository.findIdByName(candidateName);
            Long electionId = electionService.getElectionIdByName(electionName);
            
            voteService.castVote(voterId, aadhaarNumber, candidateId, electionId);
            return ResponseEntity.ok("Vote cast successfully");
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

      @PostMapping("/{voterId}/elections")
    public ResponseEntity<String> setElectionsForVoter(
            @PathVariable String voterId,
            @RequestBody Long[] electionIds) {

        List<Long> electionIdList = new ArrayList<>();

        for(Long id:electionIds){
            electionIdList.add(id);
        }

        voterService.setElectionsForVoter(voterId, electionIdList);
        
        return ResponseEntity.status(HttpStatus.OK).body("Elections set for the voter successfully");
    }

    //-------------------------- check given voter is eligible for VIDHAN_SABHA election or not
    @GetMapping(value = "/check-eligibility/state/{voterId}", produces = "application/json")
    public ResponseEntity<EligibilityResponse> checkEligibility(@PathVariable String voterId) {
        try {
            System.out.println("---------- try controller voterId = "+voterId);
            EligibilityResponse response = electionService.checkVoterEligibility(voterId);
            System.out.println("---------- done response response = "+response);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            System.out.println("--------- catch controller");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //-------------------------- check given voter is eligible for Grampanchyat election or not
    @GetMapping(value = "/check-eligibility/village/{voterId}", produces = "application/json")
    public ResponseEntity<EligibilityResponse> checkEligibilityNew(@PathVariable String voterId) {
        try {
            System.out.println("---------- try controller voterId = "+voterId);
            EligibilityResponse response = electionService.checkVoterEligibilityNew(voterId);
            System.out.println("---------- done response response = "+response);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            System.out.println("--------- catch controller");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //-------------------------- check given voter is eligible for LOK_SABHA election or not
    @GetMapping(value = "/check-eligibility/central/{voterId}", produces = "application/json")
    public ResponseEntity<EligibilityResponse> checkEligibilityForCentral(@PathVariable String voterId) {
        try {
            System.out.println("---------- try controller voterId = "+voterId);
            EligibilityResponse response = electionService.checkVoterEligibilityForCentral(voterId);
            System.out.println("---------- done response response = "+response);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            System.out.println("--------- catch controller");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //-------------------------- check given voter is eligible for NAGARSEVAK election or not
    @GetMapping(value = "/check-eligibility/city-nagarsevak/{voterId}", produces = "application/json")
    public ResponseEntity<EligibilityResponse> checkEligibilityForCentralForNagarsevak(@PathVariable String voterId) {
        try {
            System.out.println("---------- try controller voterId = "+voterId);
            EligibilityResponse response = electionService.checkVoterEligibilityForNagarsevak(voterId);
            System.out.println("---------- done response response = "+response);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            System.out.println("--------- catch controller");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //-------------------------- check given voter is eligible for NAGAR ADHYAKSH election or not
    @GetMapping(value = "/check-eligibility/city-nagaradhyakshak/{voterId}", produces = "application/json")
    public ResponseEntity<EligibilityResponse> checkEligibilityForCentralForNagarAdhyaksha(@PathVariable String voterId) {
        try {
            System.out.println("---------- try controller voterId = "+voterId);
            EligibilityResponse response = electionService.checkVoterEligibilityForNagarAdhyaksha(voterId);
            System.out.println("---------- done response response = "+response);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            System.out.println("--------- catch controller");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //--------------------------- display candidates according to valid election -------------------------
    /*@GetMapping("/voter/candidates/{aadhaarNumber}")
    public List<Candidate> getCandidatesByAadhaar(@PathVariable("aadhaarNumber") String aadhaarNumber) {
        // Step 1: Find the voter by Aadhaar number
        System.out.println("------------- start");
        Voter voter = voterRepository.findByAadhaarNumber(aadhaarNumber);
        if (voter == null) {
            // Handle case where voter is not found
            System.out.println("---------- voter null");
            return null;
        }

        // Step 2: Find all elections
        List<Election> elections = electionRepository.findAll();
        System.out.println("------------ elections = "+elections);

        // Step 3: Check eligibility for each election
        for (Election election : elections) {
            if (election.getStartTime().isBefore(LocalDateTime.now())
                    && election.getEndTime().isAfter(LocalDateTime.now())) {
                // Election is currently ongoing
                System.out.println("------------------- election if 1 ------------");
                if (isCandidateEligibleForElection(voter, election)) {
                    // Step 4: Fetch candidates for eligible election
                    System.out.println("----------------- election if 2");
                    System.out.println("--------------- elecion name = "+election.getName());
                    return candidateRepository.findByElectionName(election.getName());
                }
            }
        }

        // No eligible elections found
        return null;
    }*/


   
    @GetMapping("/candidates")
    public Map<String, List<Candidate>> getCandidatesByAadhaarOrVoterId(
        @RequestParam(value = "voterId", required = false) String voterId,
        @RequestParam(value = "aadhaarNumber", required = false) String aadhaarNumber) {
        
        Voter voter = null;
        System.out.println("hi");
        voter = voterRepository.findByVoterIdOrAadhaarNumber(voterId, aadhaarNumber).orElse(null);
        if (voter == null) {
            return Collections.emptyMap(); // or handle the case where voter is not found more gracefully
        }
    
        List<Election> elections = electionRepository.findAll();
        Map<String, List<Candidate>> candidatesByElection = new HashMap<>();
    
        for (Election election : elections) {
            if (election.getStartTime().isBefore(LocalDateTime.now()) &&
                election.getEndTime().isAfter(LocalDateTime.now())) {
                if (isCandidateEligibleForElection(voter, election)) {
                    List<Candidate> candidates = candidateRepository.findByElectionName(election.getName());
                    candidatesByElection.put(election.getName(), candidates);
                }
            }
        }
    
        return candidatesByElection;
    }

    // Step 4: Check eligibility based on assembly type and location
    private boolean isCandidateEligibleForElection(Voter voter, Election election) {
        System.out.println("------------ is candidate eligible for election");
        switch (election.getAssembly()) {
            case LOK_SABHA:
                System.out.println("------- lok sabha");
                return election.getLocation().equals(voter.getDistrict());
            case VIDHAN_SABHA:
                System.out.println("---------- vidhan sabha");
                return election.getLocation().equals(voter.getTaluka());
            case NAGARADHYAKSHA:
                System.out.println("-------- nagar adhyaksha");
                return election.getLocation().equals(voter.getCityOrVillage());
            case NAGARSEVAK:
                System.out.println("----------- nagar sevak");
                return election.getLocation().equals(voter.getCityOrVillage());
            case GRAM_PANCHAYAT:
                System.out.println("--------------- gram panchyat");
                return election.getLocation().equals(voter.getCityOrVillage());
            default:
                return false;
        }
    }

}


