package com.election.voting.service.Impl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.election.voting.model.Election;
import com.election.voting.model.Voter;
import com.election.voting.repository.ElectionRepository;
import com.election.voting.repository.VoterRepository;
import com.election.voting.service.VoterService;

import jakarta.transaction.Transactional;

@Service
public class VoterServiceImpl implements VoterService{
    
    @Autowired
    private VoterRepository voterRepository;

    @Autowired
    private ElectionRepository electionRepository;

    @Override
    public List<Voter> getAllVoters() {
        return voterRepository.findAll();
    }

    @Override
    public Voter createVoter(Voter voter) {
        return voterRepository.save(voter);
    }

    @Override
    public Voter updateVoter(Long id, Voter updatedVoter) {
        Voter voter = voterRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Voter not found"));
        
        // Update fields with new values
        voter.setFirstName(updatedVoter.getFirstName());
        voter.setLastName(updatedVoter.getLastName());
        voter.setAadhaarNumber(updatedVoter.getAadhaarNumber());
        voter.setVoterId(updatedVoter.getVoterId());

        return voterRepository.save(voter);
    }

    @Override
    public void deleteVoter(Long id) {
        if (!voterRepository.existsById(id)) {
            throw new NoSuchElementException("Voter not found");
        }
        voterRepository.deleteById(id);
    }

    @Override
    public Voter getVoterByAadhaarNumber(String aadhaarNumber) {
        return voterRepository.findByAadhaarNumber(aadhaarNumber);
    }

    @Override
    public Voter getVoterByVoterId(String voterId) {
        return voterRepository.findByVoterId(voterId);
    }

 
    @Transactional
    @Override
    public void setElectionsForVoter(String voterId, List<Long> electionIdList) {
        Voter voter = voterRepository.findByVoterId(voterId);

        List<Election> elections = electionRepository.findAllById(electionIdList);
        //Set<Election> elections = (Set<Election>) electionRepository.findAllById(electionIdList);
        voter.setElections(elections);

        voterRepository.save(voter);
    }

    
}



 