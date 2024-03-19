package com.election.voting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.election.voting.model.Election;
import com.election.voting.model.Vote;
import com.election.voting.model.Voter;

public interface VoteRepository extends JpaRepository<Vote, Long>{
    
    @Query("SELECT v.candidate, COUNT(v) FROM Vote v WHERE v.election.id = ?1 GROUP BY v.candidate")
    List<Object[]> getElectionResults(Long id);

    @Query("SELECT COUNT(v) > 0 FROM Vote v WHERE v.voter = ?1 AND v.election = ?2")
    boolean existsByVoterAndElection(Voter voter, Election election);
}
