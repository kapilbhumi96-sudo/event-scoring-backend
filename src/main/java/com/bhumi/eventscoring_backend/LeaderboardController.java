package com.bhumi.eventscoring_backend;

import com.bhumi.eventscoring_backend.dto.LeaderboardEntry;
import com.bhumi.eventscoring_backend.model.Participant;
import com.bhumi.eventscoring_backend.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:5173")
public class LeaderboardController {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private ScoringService scoringService;

    @GetMapping("/{id}/leaderboard")
    public List<LeaderboardEntry> getLeaderboard(@PathVariable Long id) {

        List<Participant> participants = participantRepository.findAll().stream()
                .filter(p -> p.getCategory().getId().equals(id))
                .collect(Collectors.toList());

        List<LeaderboardEntry> unranked = participants.stream()
                .map(p -> {
                    double finalScore = scoringService.calculateFinalScore(p.getId());
                    return new LeaderboardEntry(0, p.getUser().getName(), finalScore);
                })
                .sorted(
                        Comparator.comparingDouble(LeaderboardEntry::getFinalScore).reversed()
                                .thenComparing(entry -> scoringService.getHighestSingleRoundScore(
                                        findParticipantIdByName(participants, entry.getParticipantName())
                                ), Comparator.reverseOrder())
                )
                .collect(Collectors.toList());

        List<LeaderboardEntry> ranked = new java.util.ArrayList<>();
        int rank = 1;
        for (LeaderboardEntry entry : unranked) {
            ranked.add(new LeaderboardEntry(rank, entry.getParticipantName(), entry.getFinalScore()));
            rank++;
        }

        return ranked;
    }

    private Long findParticipantIdByName(List<Participant> participants, String name) {
        return participants.stream()
                .filter(p -> p.getUser().getName().equals(name))
                .findFirst()
                .map(Participant::getId)
                .orElseThrow();
    }
}