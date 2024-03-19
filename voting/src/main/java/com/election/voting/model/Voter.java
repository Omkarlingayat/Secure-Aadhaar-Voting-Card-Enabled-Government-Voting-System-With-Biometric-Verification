package com.election.voting.model;

import java.sql.Blob;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Voter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String firstName;
    private String middleName;
    private String lastName;
    private Date dateOfBirth;
    private String gender;
    private String contactNumber;
    @Column(name = "house_no/flat_no")
    private String houseNoFlatNo;
    @Column(name = "area/ward_no")
    private String areaOrWardNo;
    @Column(name = "city/village")
    private String cityOrVillage;
    private String taluka;
    private String pincode;
    private String district;
    private String state;
    private String country;
    private String aadhaarNumber; 
    private String voterId;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] adhaarImage;

    @Lob //Indicate a large object field
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] voterImage;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @ManyToMany
    @JoinTable(
        name = "voter_election",
        joinColumns = @JoinColumn(name = "voter_id"),
        inverseJoinColumns = @JoinColumn(name = "election_id"))
        private List<Election> elections = new ArrayList<>();

    public Voter() {
    }

       
    
}