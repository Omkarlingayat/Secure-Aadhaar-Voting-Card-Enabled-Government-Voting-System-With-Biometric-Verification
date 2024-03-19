package com.election.voting.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.election.voting.model.Voter;

@Repository
public interface VoterRepository extends JpaRepository<Voter, Long>{
    @Query("SELECT v FROM Voter v WHERE v.aadhaarNumber = ?1")
    Voter findByAadhaarNumber(String aadhaarNumber);
    
    @Query("SELECT v FROM Voter v WHERE v.voterId = ?1")
    Voter findByVoterId(String voterId);

    @Query("SELECT v.id FROM Voter v WHERE v.voterId = ?1")
    Long findIdByVoterId(String voterId);

    // @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Voter v WHERE v.voterId = ?1 AND v.election.id = ?2")
    // boolean existsByVoterIdAndElectionId(String voterId, Long electionId);

    @Query("SELECT v FROM Voter v INNER JOIN v.elections e WHERE e.id = ?1")
    List<Voter> findEligibleVotersForElection(Long electionId);

    @Query("SELECT v FROM Voter v WHERE v.id IN :voterIds")
    List<Voter> findByIdIn(List<Long> voterIds);

    
    @Query("SELECT v FROM Voter v WHERE v.country = :country")
    List<Voter> findVotersByCountry(String country);

    @Query("SELECT v FROM Voter v WHERE v.state = :state")
    List<Voter> findVotersByState(String state);

    @Query("SELECT v FROM Voter v WHERE v.cityOrVillage = :cityOrVillage")
    List<Voter> findVotersByCityOrVillage(String cityOrVillage);

    @Query("SELECT v FROM Voter v WHERE v.voterId = ?1 OR v.aadhaarNumber = ?2")
    Optional<Voter> findByVoterIdOrAadhaarNumber(String voterId, String aadhaarNumber);
}
