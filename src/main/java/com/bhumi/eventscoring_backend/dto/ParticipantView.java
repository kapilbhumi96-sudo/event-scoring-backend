package com.bhumi.eventscoring_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParticipantView {
    private Long participantId;
    private String participantName;
}
