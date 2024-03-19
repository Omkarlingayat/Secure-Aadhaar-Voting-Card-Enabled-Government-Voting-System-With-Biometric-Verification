package com.election.voting.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.election.voting.model.Election;

public interface ElectionRepository extends JpaRepository<Election, Long> {
    @Query("SELECT e FROM Election e WHERE e.id = ?1")
    Optional<Election> findById(Long id);

    @Query("SELECT e FROM Election e WHERE e.name = ?1")
    Optional<Election> findByName(String name);

    @Query("SELECT e.id FROM Election e WHERE e.name = :name")
    Long findIdByName(String name);

    @Query("SELECT e FROM Election e WHERE e.id IN :electionIds")
    List<Election> findAllByIdIn(List<Long> electionIds);

    // -------------------------- check given voter is eligible for VIDHAN_SABHA
    // election or not
    @Query("SELECT e FROM Election e WHERE :currentDateTime BETWEEN e.startTime AND e.endTime AND e.location = :state")
    List<Election> findEligibleElections(@Param("currentDateTime") LocalDateTime currentDateTime,
            @Param("state") String state);

    // -------------------------- check given voter is eligible for GramPanchyat
    // election or not
    @Query("SELECT e FROM Election e WHERE :currentDateTime BETWEEN e.startTime AND e.endTime AND e.location = :cityOrVillage")
    List<Election> findEligibleElectionsNew(@Param("currentDateTime") LocalDateTime currentDateTime,
            @Param("cityOrVillage") String cityOrVillage);

    // -------------------------- check given voter is eligible for LOK_SABHA
    // election or not
    @Query("SELECT e FROM Election e WHERE :currentDateTime BETWEEN e.startTime AND e.endTime AND e.location = :country")
    List<Election> findEligibleElectionsForCentral(@Param("currentDateTime") LocalDateTime currentDateTime,
            @Param("country") String country);

    // ------------------- election list according to admin id
    List<Election> findByAdminId(Long adminId);
}