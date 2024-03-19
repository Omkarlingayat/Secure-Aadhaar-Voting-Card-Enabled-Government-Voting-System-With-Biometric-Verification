package com.election.voting.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EligibilityResponse {
    private boolean eligible;
    private List<Long> eligibleElectionIds;
    private String message;
    private String voterId;

    public EligibilityResponse(boolean eligible, List<Long> eligibleElectionIds, String message, String voterId) {
        this.eligible = eligible;
        this.eligibleElectionIds = eligibleElectionIds;
        this.message = message;
        this.voterId = voterId;
    }

    public EligibilityResponse(){

    }
}
