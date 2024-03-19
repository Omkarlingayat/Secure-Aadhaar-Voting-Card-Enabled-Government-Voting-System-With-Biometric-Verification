package com.election.voting.response;

import com.election.voting.model.Voter;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VoterUpdateResponse {
    private Voter updatedVoter;
    private String message;
}

