package com.election.voting.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Election {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private ElectionAssembly assembly;
    private String location;

    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;

    private String status;

    @ManyToMany(mappedBy = "elections")
    private Set<Voter> voters = new HashSet<>();
    
    
    private Timestamp createdAt;
    private Timestamp updatedAt;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    public Election(Long id, String name, ElectionAssembly assembly, String location, LocalDateTime startTime,
            LocalDateTime endTime, String status, Timestamp createdAt, Timestamp updatedAt, Admin admin) {
        this.id = id;
        this.name = name;
        this.assembly = assembly;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.admin = admin;
    }





    public Election() {
    }

    
}