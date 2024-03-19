package com.election.voting.model;

import java.sql.Timestamp;

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

@Entity
@Data
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)

    private String mobile;

    private String email;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdminRole role;

    @Column(name = "house_no/flat_no")
    private String houseNoFlatNo;
    @Column(name = "city/village")
    private String cityOrVillage;
    private String taluka;
    private String pincode;
    private String district;
    private String state;
    private String country;

    @ManyToOne
    @JoinColumn(name = "created_admin_id")
    private Admin admin;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    public Admin(Long id, String username, String password, String mobile, String email, String fullName,
            AdminRole role, Timestamp createdAt, String houseNoFlatNo, String cityOrVillage, String taluka,
            String pincode, Admin admin,
            String district, String state, String country) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.mobile = mobile;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.createdAt = createdAt;
        this.houseNoFlatNo = houseNoFlatNo;
        this.cityOrVillage = cityOrVillage;
        this.taluka = taluka;
        this.pincode = pincode;
        this.district = district;
        this.state = state;
        this.country = country;
        this.admin = admin;
    }

    public Admin() {
    }

}