package com.election.voting.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VoterDTO {
    private Long id;
    private String cityOrVillage;
    private String country;
    private String state;
    private String voterId;

}
