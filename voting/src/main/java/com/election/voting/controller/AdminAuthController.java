package com.election.voting.controller;

import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.election.voting.config.JwtProvider;
import com.election.voting.exception.AdminException;
import com.election.voting.model.Admin;
import com.election.voting.repository.AdminRepository;
import com.election.voting.response.AuthResponse;
import com.election.voting.service.Impl.AdminServiceImpl;

@RestController
@RequestMapping("/admin")
public class AdminAuthController {

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdminServiceImpl adminServiceImpl;

    @GetMapping("/profile")
    public ResponseEntity<Admin> getAdminProfileHandler(@RequestHeader("Authorization") String jwt)
            throws AdminException {

        Admin admin = adminServiceImpl.findAdminProfileByJwt(jwt);

        return new ResponseEntity<Admin>(admin, HttpStatus.ACCEPTED);
    }

    // ---------------------------- create central admi
    // ------------------------------
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> adminSignup(@RequestBody Admin admin) throws AdminException {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        Admin username = adminRepository.findByUsername(admin.getUsername());
        Admin email = adminRepository.findByEmail(admin.getEmail());
        if (username != null) {
            throw new AdminException("username already exists. try differrent");
        }
        if (email != null) {
            throw new AdminException("email already exists. try differrent");
        }
        admin.setCreatedAt(Timestamp.from(Instant.now()));
        if (admin.getRole() == null) {
            admin.setRole(admin.getRole());
        }

        adminRepository.save(admin);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setMessage("Admin created successfully");
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    // ---------------------------- create other admins -----------------
  

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> adminSignin(@RequestBody Admin admin) throws AdminException {

        Authentication authentication = authenticate(admin.getUsername(), admin.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("admin signin successfully");
        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = adminServiceImpl.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username..");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password..");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

}