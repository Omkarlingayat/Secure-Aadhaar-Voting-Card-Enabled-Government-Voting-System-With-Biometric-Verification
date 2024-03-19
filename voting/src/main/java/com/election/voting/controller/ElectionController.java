package com.election.voting.controller;

import java.time.LocalDateTime;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.election.voting.DTO.VoterDTO;
import com.election.voting.exception.AdminException;
import com.election.voting.model.Admin;
import com.election.voting.model.Election;
import com.election.voting.model.Voter;
import com.election.voting.repository.ElectionRepository;
import com.election.voting.repository.VoterRepository;
import com.election.voting.response.ElectionResponse;
import com.election.voting.response.ElectionUpdateResponse;
import com.election.voting.response.VoterListResponse;
import com.election.voting.service.ElectionService;
import com.election.voting.service.Impl.AdminServiceImpl;

@RestController
@RequestMapping("/api/admin")
public class ElectionController {

    @Autowired
    private ElectionService electionService;

    @Autowired
    private AdminServiceImpl adminServiceImpl;

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private VoterRepository voterRepository;

    @PostMapping("/create/election")
    public ResponseEntity<ElectionResponse> createElection(@RequestHeader("Authorization") String jwt,
            @RequestBody Election election) {
        try {
            Admin admin = adminServiceImpl.findAdminProfileByJwt(jwt);
            election.setAdmin(admin);

            ElectionResponse response = electionService.createElection(election, admin);
            if (response.getElection() != null) {
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/election/{electionName}")
    public ResponseEntity<Election> getElection(@RequestHeader("Authorization") String jwt,
            @PathVariable("electionName") String electionName) {
        try {
            Long id = electionService.getElectionIdByName(electionName);
            Election election = electionService.getElectionById(id);
            return new ResponseEntity<>(election, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{electionId}")
    public ResponseEntity<ElectionUpdateResponse> updateElection(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long electionId,
            @RequestBody Election election) {
        try {
            Admin admin = adminServiceImpl.findAdminProfileByJwt(jwt);
            election.setAdmin(admin);
            ElectionUpdateResponse response = electionService.updateElection(electionId, election, admin);
            if (response.getUpdatedElection() != null) {
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/election/{electionName}")
    public ResponseEntity<String> deleteElection(@RequestHeader("Authorization") String jwt,
            @PathVariable("electionName") String electionName) {
        try {
            Long id = electionService.getElectionIdByName(electionName);
            electionService.deleteElection(id);
            return new ResponseEntity<>("Election deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // display election by its name
    @GetMapping("/election/{name}")
    public ResponseEntity<Election> getElectionByName(@RequestHeader("Authorization") String token,
            @PathVariable("name") String name) throws AdminException {
        Admin admin = adminServiceImpl.findAdminProfileByJwt(token);

        Optional<Election> opt = electionRepository.findByName(name);

        if (opt.isPresent()) {
            Election election = opt.get();

            if (election.getAdmin().getId() == admin.getId()) {
                return new ResponseEntity<Election>(election, HttpStatus.ACCEPTED);
            } else {
                throw new AdminException("You don't have access");
            }
        } else {
            throw new AdminException("election not found");
        }
    }

    // display election list
    @GetMapping("/election")
    public ResponseEntity<List<Election>> getElectionList(@RequestHeader("Authorization") String token)
            throws AdminException {
        Admin admin = adminServiceImpl.findAdminProfileByJwt(token);
        Long adminId = admin.getId();
        List<Election> electionList = electionRepository.findByAdminId(adminId);

        return new ResponseEntity<>(electionList, HttpStatus.OK);
    }

    @GetMapping("/by-election/{electionId}")
    public ResponseEntity<VoterListResponse> getVotersByElection(@PathVariable Long electionId) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new NoSuchElementException("Election not found"));

        LocalDateTime currentDateTime = LocalDateTime.now();

        if (currentDateTime.isBefore(election.getStartTime()) || currentDateTime.isAfter(election.getEndTime())) {
            return ResponseEntity.badRequest().body(new VoterListResponse(Collections.emptyList()));
        }

        List<VoterDTO> voters = new ArrayList<>();

        switch (election.getAssembly()) {
            case LOK_SABHA:
                voters = voterRepository.findVotersByCountry(election.getLocation())
                        .stream()
                        .map(this::mapToVoterDTO)
                        .collect(Collectors.toList());
                break;
            case VIDHAN_SABHA:
                voters = voterRepository.findVotersByState(election.getLocation())
                        .stream()
                        .map(this::mapToVoterDTO)
                        .collect(Collectors.toList());
                break;
            default:
                voters = voterRepository.findVotersByCityOrVillage(election.getLocation())
                        .stream()
                        .map(this::mapToVoterDTO)
                        .collect(Collectors.toList());
                break;
        }

        return ResponseEntity.ok(new VoterListResponse(voters));
    }

    private VoterDTO mapToVoterDTO(Voter voter) {
        VoterDTO voterDTO = new VoterDTO();
        voterDTO.setId(voter.getId());
        voterDTO.setCityOrVillage(voter.getCityOrVillage());
        voterDTO.setCountry(voter.getCountry());
        voterDTO.setState(voter.getState());
        voterDTO.setVoterId(voter.getVoterId());
        return voterDTO;
    }
}