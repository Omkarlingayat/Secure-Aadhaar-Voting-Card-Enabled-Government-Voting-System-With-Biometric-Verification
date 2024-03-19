package com.election.voting.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.election.voting.model.Admin;


@Repository
public interface AdminRepository extends JpaRepository<Admin, Long>{
    
    public Admin findByUsername(String username);

    public Admin findByEmail(String email);

}
