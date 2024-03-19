package com.election.voting.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.sql.Time;
import java.sql.Timestamp;

@Entity
@Data
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String party;

    @JoinColumn(name = "election_name")
    private String electionName;

    @Column(name = "house_no/flat_no")
    private String houseNoFlatNo;
    @Column(name = "city/village")
    private String cityOrVillage;
    private String taluka;
    private String pincode;
    private String district;
    private String state;
    private String country;


    private Timestamp createdAt;
    private Timestamp updatedAt;

    @Enumerated(EnumType.STRING)
    private ElectionAssembly assembly;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    public Candidate(Long id, String name, String party, String electionName,
            String houseNoFlatNo, String cityOrVillage, String taluka, String pincode, String district, String state,
            String country, Timestamp createdAt, Timestamp updatedAt, ElectionAssembly assembly, Admin admin) {
        this.id = id;
        this.name = name;
        this.party = party;
        this.electionName = electionName;
        this.houseNoFlatNo = houseNoFlatNo;
        this.cityOrVillage = cityOrVillage;
        this.taluka = taluka;
        this.pincode = pincode;
        this.district = district;
        this.state = state;
        this.country = country;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.assembly = assembly;
        this.admin = admin;
    }



    public Candidate() {
    }
}