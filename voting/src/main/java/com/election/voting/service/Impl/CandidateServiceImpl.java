package com.election.voting.service.Impl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.election.voting.exception.AdminException;
import com.election.voting.model.Admin;
import com.election.voting.model.AdminRole;
import com.election.voting.model.Candidate;
import com.election.voting.model.ElectionAssembly;
import com.election.voting.repository.CandidateRepository;
import com.election.voting.service.CandidateService;

@Service
public class CandidateServiceImpl implements CandidateService{
    
     @Autowired
    private CandidateRepository candidateRepository;

    @Override
    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    @Override
    public Candidate getCandidateById(Long id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Candidate not found"));
    }

    @Override
    public Candidate createCandidate(Admin admin, Candidate candidate) {
        // ----------------------- set election assembly according to Admin role
        if (admin.getRole() == AdminRole.CITY_NAGAR_ADHYAKSHA) {
            candidate.setAssembly(ElectionAssembly.NAGARADHYAKSHA);
        } else if (admin.getRole() == AdminRole.CITY_NAGAR_SEVAK) {
            candidate.setAssembly(ElectionAssembly.NAGARSEVAK);
        } else if (admin.getRole() == AdminRole.VILLAGE){
            candidate.setAssembly(ElectionAssembly.GRAM_PANCHAYAT);
        }

        candidate.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        Candidate createCandidate = candidateRepository.save(candidate);
        return createCandidate;
    }

    @Override
    public Candidate updateCandidate(String name, Candidate updatdCandidate, Admin admin) throws Exception {

        Candidate candidate = candidateRepository.findCandidateByName(name);
        Long id = candidate.getId();
        Optional<Candidate> existingCandidate = candidateRepository.findById(id);
        if (existingCandidate.isPresent()) {
            Candidate currentCandidate = existingCandidate.get();

            // Check if the admin has access to update this election

            if (admin.getRole() == currentCandidate.getAdmin().getRole()) {
                // Use reflection to update non-null fields of the current election with the new
                // values
                Field[] fields = Candidate.class.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    Object updatedValue = field.get(updatdCandidate);
                    if (updatedValue != null) {
                        field.set(currentCandidate, updatedValue);
                    }
                }

                currentCandidate.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

                Candidate updated = candidateRepository.save(currentCandidate);
                return updated;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    // -------------- delete candidate by name
    @Override
    public void deleteCandidate(Admin admin, Candidate candidate) {
       
            Long id = candidate.getId();
            candidateRepository.deleteById(id);
  
    }


}
