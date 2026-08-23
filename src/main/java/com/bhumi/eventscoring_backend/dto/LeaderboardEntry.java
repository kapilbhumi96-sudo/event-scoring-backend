package com.bhumi.eventscoring_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeaderboardEntry {
    private int rank;
    private String participantName;
    private double finalScore;
}

// Lombok automatically generates getters, setters, toString, equals, hashCode, etc.
// Lombok automatically creates a constructor accepting rank, participantName, and finalScore.
// DTO used to send only the information the leaderboard needs instead of exposing the complete
// Participant/Score objects.
// Stores the participant's final leaderboard position.
// Stores the name displayed on the leaderboard.
// Stores the calculated final score.