package com.election.voting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.election.voting.model.Candidate;
import com.election.voting.model.ElectionAssembly;

public interface CandidateRepository extends JpaRepository<Candidate, Long>{
    
    @Query("SELECT c.id FROM Candidate c WHERE c.name = :name")
    Long findIdByName(String name);

    @Query("SELECT c FROM Candidate c WHERE c.name = :name")
    Candidate findCandidateByName(@Param("name") String name);

    @Query("SELECT c FROM Candidate c WHERE c.admin.id = :adminId AND c.assembly = :assembly")
    List<Candidate> findCandidateByAdminIdAndAssembly(@Param("adminId") Long adminId, @Param("assembly") ElectionAssembly assembly);

    @Query("SELECT c FROM Candidate c WHERE c.electionName = :electionName")
    List<Candidate> findByElectionName(String electionName);
}