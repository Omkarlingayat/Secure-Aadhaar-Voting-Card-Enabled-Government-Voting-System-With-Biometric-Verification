package com.election.voting.service.Impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ssl.SslProperties.Bundles.Watch.File;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.election.voting.model.Candidate;
import com.election.voting.model.Election;
import com.election.voting.model.Vote;
import com.election.voting.model.Voter;
import com.election.voting.repository.CandidateRepository;
import com.election.voting.repository.ElectionRepository;
import com.election.voting.repository.VoteRepository;
import com.election.voting.repository.VoterRepository;
import com.election.voting.service.VoteService;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.IOException;

@Service
public class VoteServiceImpl implements VoteService{
    
    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private VoterRepository voterRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void castVote(String voterId,String aadhaarNumber, Long candidateId, Long electionId) {
        // Check if the voter, candidate, and election exist
        Voter voter = voterRepository.findByVoterIdOrAadhaarNumber(voterId, aadhaarNumber)
            .orElseThrow(() -> new NoSuchElementException("Voter not found"));

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new NoSuchElementException("Candidate not found"));

        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new NoSuchElementException("Election not found"));

        // Check if the voter is eligible to vote in the election (additional checks may be needed)
        // if (!voter.isEligibleForElection(election)) {
        //     throw new IllegalStateException("Voter is not eligible to vote in this election");
        // }

        // Check if the voter has already voted in this election (additional checks may be needed)
        if (voteRepository.existsByVoterAndElection(voter, election)) {
            throw new IllegalStateException("Voter has already casted vote in this election");
        }

        // Record the vote
        Vote vote = new Vote();
        vote.setVoter(voter);
        //vote.setVoterId(passwordEncoder.encode(id));
        vote.setCandidate(candidate);
        vote.setElection(election);
        voteRepository.save(vote);
    }

    @Override
    public Map<Candidate, Long> getElectionResults(Long electionId) throws IOException, FileNotFoundException {
        List<Object[]> results = voteRepository.getElectionResults(electionId);
        Map<Candidate, Long> electionResults = new HashMap<>();
        for (Object[] result : results) {
            Candidate candidate = (Candidate) result[0];
            Long voteCount = (Long) result[1];
            electionResults.put(candidate, voteCount);
        }

        // Get user's home directory
        String userHome = System.getProperty("user.home");

        // Define the file name and path
        String fileName = "election_results.pdf";
        String filePath = userHome + "/Downloads/" + fileName;

        // Create a new PDF document
        PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        // Add title
        PdfFont font = PdfFontFactory.createFont("Helvetica");
        Paragraph title = new Paragraph("Election Results").setFont(font);
        document.add(title);

        // Add results
        for (Entry<Candidate, Long> entry : electionResults.entrySet()) {
            Paragraph result = new Paragraph("Candidate ID: " + entry.getKey() + ", Votes: " + entry.getValue());
            document.add(result);
        }

        // Close the document
        document.close();
        
        return electionResults;
    }

    // @Override
    // public void generateElectionResultsPdf(Long electionId) throws IOException, FileNotFoundException {
    //     Map<Candidate, Long> results = getElectionResults(electionId);

    //     // Get user's home directory
    //     String userHome = System.getProperty("user.home");

    //     // Define the file name and path
    //     String fileName = "election_results.pdf";
    //     String filePath = userHome + "/Downloads/" + fileName;

    //     // Create a new PDF document
    //     PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
    //     PdfDocument pdfDocument = new PdfDocument(writer);
    //     Document document = new Document(pdfDocument);

    //     // Add title
    //     PdfFont font = PdfFontFactory.createFont("Helvetica");
    //     Paragraph title = new Paragraph("Election Results").setFont(font);
    //     document.add(title);

    //     // Add results
    //     for (Entry<Candidate, Long> entry : results.entrySet()) {
    //         Paragraph result = new Paragraph("Candidate ID: " + entry.getKey() + ", Votes: " + entry.getValue());
    //         document.add(result);
    //     }

    //     // Close the document
    //     document.close();
    // }
}
