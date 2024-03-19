package com.election.voting.response;

import com.election.voting.model.Election;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ElectionUpdateResponse {
    private Election updatedElection;
    private String message;

    // Constructors, getters, and setters

    public ElectionUpdateResponse(Election updatedElection, String message) {
        this.updatedElection = updatedElection;
        this.message = message;
    }

    public Election getUpdatedElection() {
        return updatedElection;
    }

    public void setUpdatedElection(Election updatedElection) {
        this.updatedElection = updatedElection;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

