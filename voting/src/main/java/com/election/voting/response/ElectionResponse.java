package com.election.voting.response;

import com.election.voting.model.Election;


public class ElectionResponse {
    private Election election;
    private String message;
    public Election getElection() {
        return election;
    }
    public void setElection(Election election) {
        this.election = election;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public ElectionResponse() {
    }
    public ElectionResponse(Election election, String message) {
        this.election = election;
        this.message = message;
    }

    
}