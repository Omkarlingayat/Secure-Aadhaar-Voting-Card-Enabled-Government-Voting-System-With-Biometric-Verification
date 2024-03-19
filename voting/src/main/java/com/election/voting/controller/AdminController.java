package com.election.voting.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.election.voting.exception.AdminException;
import com.election.voting.model.Admin;
import com.election.voting.model.AdminRole;
import com.election.voting.model.Candidate;
import com.election.voting.model.ElectionAssembly;
import com.election.voting.model.Voter;
import com.election.voting.repository.AdminRepository;
import com.election.voting.repository.CandidateRepository;
import com.election.voting.repository.VoterRepository;
import com.election.voting.response.ApiResponse;
import com.election.voting.response.AuthResponse;
import com.election.voting.service.CandidateService;
import com.election.voting.service.VoteService;
import com.election.voting.service.VoterService;
import com.election.voting.service.Impl.AdminServiceImpl;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private CandidateService candidateService;

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    private VoterService voterService;

    @Autowired
    private VoterRepository voterRepository;
    
    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private VoteService voteService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired 
    private AdminServiceImpl adminServiceImpl;

    @GetMapping("/profile")
    public ResponseEntity<Admin> getAdminRole(@RequestHeader("Authorization")String jwt)throws AdminException{

        Admin admin = adminServiceImpl.findAdminProfileByJwt(jwt);

        return new ResponseEntity<Admin>(admin, HttpStatus.ACCEPTED);
    }

    @GetMapping("/get/role")
    public ResponseEntity<Map<String, String>> getAdminProfileHandler(@RequestHeader("Authorization") String jwt) throws AdminException {
        Admin admin = adminServiceImpl.findAdminProfileByJwt(jwt);
        String role = admin.getRole().toString();
        System.out.println(admin.getRole());
        Map<String, String> roleMap = new HashMap<>();
        roleMap.put("role", role);
        return new ResponseEntity<>(roleMap, HttpStatus.OK);
    }

      @PostMapping("/create")
    public ResponseEntity<AuthResponse> createAdmin(@RequestBody Admin admin,
            @RequestHeader("Authorization") String token) throws AdminException {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        Admin username = adminRepository.findByUsername(admin.getUsername());

        Admin createdAdmin = adminServiceImpl.findAdminProfileByJwt(token);
        admin.setAdmin(createdAdmin);

        if (username != null) {
            throw new AdminException("username already exists. try differrent");
        }

        admin.setCreatedAt(Timestamp.from(Instant.now()));

        if (admin.getRole() == null) {
            admin.setRole(admin.getRole());
        }

        if (createdAdmin.getRole() == AdminRole.CENTRAL) {
            admin.setRole(AdminRole.STATE);
        } else if (createdAdmin.getRole() == AdminRole.STATE) {
            admin.setRole(AdminRole.CITY_NAGAR_ADHYAKSHA);
        } else if (createdAdmin.getRole() == AdminRole.CITY_NAGAR_ADHYAKSHA) {
            admin.setRole(AdminRole.CITY_NAGAR_SEVAK);
        } else if (createdAdmin.getRole() == AdminRole.CITY_NAGAR_SEVAK) {
            admin.setRole(AdminRole.VILLAGE);
        }

        adminRepository.save(admin);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setMessage("Admin created successfully");

        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @GetMapping("/getByEmail/{email}")
    public ResponseEntity<Admin> getAdminByEmail(@PathVariable String email,@RequestHeader("Authorization") String token) throws AdminException {
        Admin admin = adminServiceImpl.findByEmail(email);
       
        try {
            return new ResponseEntity<Admin>(admin, HttpStatus.OK);
        } catch (Exception e) {
            throw new AdminException("Admin does not exist by email");
        }
        
    }

    @PutMapping("/updateByEmail/{email}")
    public ResponseEntity<?> updateAdmin(@PathVariable String email, @RequestBody Admin updatedAdmin,
            @RequestHeader("Authorization") String token) {
        try {
            Admin createdAdmin = adminServiceImpl.findAdminProfileByJwt(token);
            Admin existingAdmin = adminServiceImpl.findByEmail(email);

            if (existingAdmin == null) {
                return ResponseEntity.notFound().build();
            }

            // Update only the provided fields
            if (updatedAdmin.getUsername() != null) {
                existingAdmin.setUsername(updatedAdmin.getUsername());
            }
            if (updatedAdmin.getPassword() != null) {
                existingAdmin.setPassword(passwordEncoder.encode(updatedAdmin.getPassword()));
            }
            if (updatedAdmin.getMobile() != null) {
                existingAdmin.setMobile(updatedAdmin.getMobile());
            }
            if (updatedAdmin.getEmail() != null) {
                existingAdmin.setEmail(updatedAdmin.getEmail());
            }
            if (updatedAdmin.getFullName() != null) {
                existingAdmin.setFullName(updatedAdmin.getFullName());
            }
            if (updatedAdmin.getHouseNoFlatNo() != null) {
                existingAdmin.setHouseNoFlatNo(updatedAdmin.getHouseNoFlatNo());
            }
            
            if (updatedAdmin.getCityOrVillage() != null) {
                existingAdmin.setCityOrVillage(updatedAdmin.getCityOrVillage());
            }
            if (updatedAdmin.getTaluka() != null) {
                existingAdmin.setTaluka(updatedAdmin.getTaluka());
            }
            if (updatedAdmin.getPincode() != null) {
                existingAdmin.setPincode(updatedAdmin.getPincode());
            }
            if (updatedAdmin.getDistrict() != null) {
                existingAdmin.setDistrict(updatedAdmin.getDistrict());
            }
            if (updatedAdmin.getState() != null) {
                existingAdmin.setState(updatedAdmin.getState());
            }
            if (updatedAdmin.getCountry() != null) {
                existingAdmin.setCountry(updatedAdmin.getCountry());
            }

            if (createdAdmin == existingAdmin) {
                Admin savedAdmin = adminServiceImpl.updateAdmin(existingAdmin);
                return ResponseEntity.ok(savedAdmin);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this admin.");
            }
        } catch (AdminException e) {
            return ResponseEntity.badRequest().body("Error updating admin: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error while updating admin: " + e.getMessage());
        }
    }

    @DeleteMapping("deleteByEmail/{email}")
    public ResponseEntity<ApiResponse> deleteAdminByEmail(@RequestHeader("Authorization") String token,
            @PathVariable("email") String email) throws AdminException {
        Admin createAdmin = adminServiceImpl.findAdminProfileByJwt(token);
        Admin existingAdmin = adminRepository.findByEmail(email);

        ApiResponse response = new ApiResponse();
        response.setMessage("Admin deleted successfully");
        response.setStatus(true);
        if (existingAdmin.getAdmin() == createAdmin) {
            Long id = existingAdmin.getId();
            adminRepository.deleteById(id);
            return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
        }
        throw new AdminException("Admin not found");
    }

    
    // Create Candidate
    @PostMapping("/create/candidate")
    public ResponseEntity<ApiResponse> createCandidate(@RequestHeader("Authorization") String jwt,
            @RequestBody Candidate candidate) {
        try {
            Admin admin = adminServiceImpl.findAdminProfileByJwt(jwt);
            candidate.setAdmin(admin);
            Candidate newCandidate = candidateService.createCandidate(admin, candidate);
            ApiResponse response = new ApiResponse();
            response.setMessage("Candidate created successfully");
            if (newCandidate != null) {
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            ApiResponse response = new ApiResponse();
            response.setMessage("There is a problem so candidate not created");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/candidate/{name}")
    public ResponseEntity<Candidate> getCandidateByName(
        @RequestHeader("Authorization") String jwt,
        @PathVariable String name) {
    try {
        System.out.println(name);
        Candidate existingCandidate = candidateRepository.findCandidateByName(name);
    
        // ApiResponse response = new ApiResponse();
        // response.setMessage("There is error so candidate not updated");
        return new ResponseEntity<Candidate>(existingCandidate, HttpStatus.OK);
    } catch (Exception e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
        
    }

    @GetMapping("/get")
    public ResponseEntity<List<Candidate>> getAllCandidates(@RequestHeader("Authorization") String jwt) throws AdminException {
        Admin admin = adminServiceImpl.findAdminProfileByJwt(jwt);
        System.out.println("hi");
        Long id = admin.getId();
        List<Candidate> candidate = candidateService.getAllCandidates();
        return new ResponseEntity<List<Candidate>>(candidate, null, HttpStatus.OK);
    }

    @GetMapping("/get/candidate-list/{assemblyName}")
    public ResponseEntity<List<Candidate>> getCandidate(@RequestHeader("Authorization") String jwt, @PathVariable String assemblyName) throws AdminException {
        Admin admin = adminServiceImpl.findAdminProfileByJwt(jwt);
        System.out.println("hi");
        Long adminId = admin.getId();

        // Convert assemblyName to ElectionAssembly enum
        ElectionAssembly assembly;
        try {
            assembly = ElectionAssembly.valueOf(assemblyName.toUpperCase());
            System.out.println(assembly);
        } catch (IllegalArgumentException e) {
            // Handle the case where the assemblyName does not match any enum value
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        List<Candidate> candidate = candidateRepository.findCandidateByAdminIdAndAssembly(adminId, assembly);
        return new ResponseEntity<>(candidate, HttpStatus.OK);
    }

    // ---------------------------------- Update Candidate (only that admin have
    // access which is created)
    @PutMapping("/update/candidate/{name}")
    public ResponseEntity<ApiResponse> updateCandidate(
            @RequestHeader("Authorization") String jwt,
            @PathVariable String name,
            @RequestBody Candidate candidate) {
        try {
            Admin admin = adminServiceImpl.findAdminProfileByJwt(jwt);
            candidate.setAdmin(admin);
            Candidate updatedCandidate = candidateService.updateCandidate(name, candidate, admin);
            ApiResponse response = new ApiResponse();
            response.setMessage("Candidate updated successfully");
            if (updatedCandidate != null) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            ApiResponse response = new ApiResponse();
            response.setMessage("There is error so candidate not updated");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete Candidate by name
    @DeleteMapping("/delete/candidate/{name}")
    public ResponseEntity<ApiResponse> deleteCandidate(@PathVariable("name") String name,
            @RequestHeader("Authorization") String token) throws AdminException {

        Admin admin = adminServiceImpl.findAdminProfileByJwt(token);
        Candidate candidate = candidateRepository.findCandidateByName(name);
        candidateService.deleteCandidate(admin, candidate);
        ApiResponse res = new ApiResponse();
        res.setMessage("Candidate deleted ");
        res.setStatus(true);
        
        return new ResponseEntity<ApiResponse>(res, HttpStatus.OK);
    }

    //create voter
    // @PostMapping("/create/voter")
    // public ResponseEntity<Voter> createVoter(@RequestHeader("Authorization")String jwt, @RequestBody Voter voter) throws AdminException {
    //     Admin admin = adminServiceImpl.findAdminProfileByJwt(jwt);
    //     voter.setAdmin(admin);
    //     System.out.println(jwt);
    //     Voter isVoterExistByAadhaar = voterService.getVoterByAadhaarNumber(voter.getAadhaarNumber());
    //     Voter isVoterExistByVoterId = voterService.getVoterByVoterId(voter.getVoterId());

    //     if(isVoterExistByAadhaar != null || isVoterExistByVoterId !=null){
    //         throw new AdminException("Voter is already registered");
    //     }

    //     Voter savedVoter = voterService.createVoter(voter);
    //     return new ResponseEntity<>(savedVoter, HttpStatus.OK);
    // }

    
    // create voter
   @PostMapping("/create/voter")
    public ResponseEntity<Voter> createVoter(
            @RequestHeader("Authorization") String jwt,
            @RequestParam("firstName") String firstName,
            @RequestParam("middleName") String middleName,
            @RequestParam("lastName") String lastName,
            @RequestParam("dateOfBirth") String dateOfBirth,
            @RequestParam("gender") String gender,
            @RequestParam("contactNumber") String contactNumber,
            @RequestParam("houseNoFlatNo") String houseNoFlatNo,
            @RequestParam("areaOrWardNo") String areaOrWardNo,
            @RequestParam("cityOrVillage") String cityOrVillage,
            @RequestParam("taluka") String taluka,
            @RequestParam("pincode") String pincode,
            @RequestParam("district") String district,
            @RequestParam("state") String state,
            @RequestParam("country") String country,
            @RequestParam("aadhaarNumber") String aadhaarNumber,
            @RequestParam("voterId") String voterId,
            @RequestParam("aadhaarImage") MultipartFile adhaarImage,
            @RequestParam("voterImage") MultipartFile voterImage
    ) throws AdminException, IOException, ParseException {

            
        if (adhaarImage.getSize() > 256 * 1024 || voterImage.getSize() > 256 * 1024) {
            throw new AdminException("Image size exceeds 256KB limit");
        }

        Voter voter = new Voter();
        Admin admin = adminServiceImpl.findAdminProfileByJwt(jwt);
        voter.setAdmin(admin);

        Voter isVoterExistByAadhaar = voterService.getVoterByAadhaarNumber(voter.getAadhaarNumber());
        Voter isVoterExistByVoterId = voterService.getVoterByVoterId(voter.getVoterId());

        if(isVoterExistByAadhaar != null || isVoterExistByVoterId !=null){
            throw new AdminException("Voter is already registered");
        }

        voter.setFirstName(firstName);
        voter.setMiddleName(middleName);
        voter.setLastName(lastName);


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse(dateOfBirth);
        voter.setDateOfBirth(date);
        voter.setGender(gender);
        voter.setContactNumber(contactNumber);
        voter.setHouseNoFlatNo(houseNoFlatNo);
        voter.setAreaOrWardNo(areaOrWardNo);
        voter.setCityOrVillage(cityOrVillage);
        voter.setTaluka(taluka);
        voter.setPincode(pincode);
        voter.setDistrict(district);
        voter.setState(state);
        voter.setCountry(country);
        voter.setAadhaarNumber(aadhaarNumber);
        voter.setVoterId(voterId);
        voter.setAdhaarImage(adhaarImage.getBytes());
        voter.setVoterImage(voterImage.getBytes());

        // Your existing logic to create the voter.

        Voter savedVoter = voterService.createVoter(voter);
        return new ResponseEntity<>(savedVoter, HttpStatus.CREATED);
    }

    @GetMapping("/voter/{voterId}/adhaarImage")
    public ResponseEntity<byte[]> getAdhaarImage(@RequestHeader("Authorization") String jwt,@PathVariable String voterId) {
        Voter voter = voterRepository.findByVoterId(voterId);
        byte[] adhaarImage = voter.getAdhaarImage();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(adhaarImage, headers, HttpStatus.OK);
    }

    @GetMapping("/voter/{voterId}/voterImage")
    public ResponseEntity<byte[]> getVoterImage(@RequestHeader("Authorization") String jwt,@PathVariable String voterId) {
        Voter voter = voterRepository.findByVoterId(voterId);
        byte[] voterImage = voter.getVoterImage();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(voterImage, headers, HttpStatus.OK);
    }

    @GetMapping("/get/voter/{voterId}")
    public ResponseEntity<Voter> getVoter(@RequestHeader("Authorization")String jwt,@PathVariable("voterId") String voterId){
        try {
            Voter existingVoter = voterRepository.findByVoterId(voterId);
         
            return new ResponseEntity<>(existingVoter, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //   @GetMapping("/get/voter/{voterId}")
    // public ResponseEntity<Map<String, Object>> getVoterWithImages(@RequestHeader("Authorization") String jwt, @PathVariable("voterId") String voterId) {
    //     try {
    //         Voter existingVoter = voterRepository.findByVoterId(voterId);
    //         System.out.println(existingVoter);
    //         if (existingVoter == null) {
    //             return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    //         }

    //         byte[] adhaarImage = existingVoter.getAdhaarImage();
    //         byte[] voterImage = existingVoter.getVoterImage();
    //         String encodedAdhaarImage = Base64Utils.encodeToString(adhaarImage);
    //         String encodedVoterImage = Base64Utils.encodeToString(voterImage);

    //         Map<String, Object> response = new HashMap<>();
    //         response.put("voter", existingVoter); // Adjust according to how you want to serialize Voter object
    //         response.put("adhaarImage", encodedAdhaarImage);
    //         response.put("voterImage", encodedVoterImage);

    //         return new ResponseEntity<>(response, HttpStatus.OK);
    //     } catch (Exception e) {
    //         return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    //     }
    // }

    @PutMapping("/update/voter/{voterId}")
    public ResponseEntity<Voter> updateVoter(@RequestHeader("Authorization")String jwt,@PathVariable("voterId") String voterId, @RequestBody Voter updatedVoterData) {
        try {
            Voter existingVoter = voterRepository.findByVoterId(voterId);
            if (existingVoter == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
    
            // Update only the attributes that are present in the request body
            if (updatedVoterData.getFirstName() != null) {
                existingVoter.setFirstName(updatedVoterData.getFirstName());
            }
            if (updatedVoterData.getLastName() != null) {
                existingVoter.setLastName(updatedVoterData.getLastName());
            }
            
            Voter savedVoter = voterRepository.save(existingVoter);
            return new ResponseEntity<>(savedVoter, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get Election Results
    @GetMapping("/results/{electionId}")
    public ResponseEntity<?> getElectionResults(@PathVariable Long electionId) throws FileNotFoundException, IOException {
        try {
            Map<Candidate, Long> electionResults = voteService.getElectionResults(electionId);
            return new ResponseEntity<>(electionResults, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Election not found", HttpStatus.NOT_FOUND);
        }
    }

    // @GetMapping("/results/file/{electionId}")
    // public ResponseEntity<?> getElectionResultsInFile(@PathVariable Long electionId) {
    //     try {
    //         Map<Candidate, Long> electionResults = voteService.getElectionResultsPdf(electionId);
    //         return new ResponseEntity<>(electionResults, HttpStatus.OK);
    //     } catch (NoSuchElementException e) {
    //         return new ResponseEntity<>("Election not found", HttpStatus.NOT_FOUND);
    //     }
    // }

    // Set Election Dates
    // @PutMapping("/elections/{id}/dates")
    // public ResponseEntity<?> setElectionDates(@PathVariable("id") Long id, @RequestParam("startDate") LocalDate startDate, @RequestParam("endDate") LocalDate endDate) {
    //     try {
    //         electionService.enableElection(id, startDate, endDate);
    //         return new ResponseEntity<>(HttpStatus.OK);
    //     } catch (NoSuchElementException e) {
    //         return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    //     }
    // }
}
