package com.election.voting.response;
import java.util.List;

import com.election.voting.DTO.VoterDTO;

public class VoterListResponse {
    private List<VoterDTO> voters;

    public VoterListResponse(List<VoterDTO> voters) {
        this.voters = voters;
    }

    public List<VoterDTO> getVoters() {
        return voters;
    }

    public void setVoters(List<VoterDTO> voters) {
        this.voters = voters;
    }
}

