package com.bhumi.eventscoring_backend;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.bhumi.eventscoring_backend.dto.ScoreRequest;
import com.bhumi.eventscoring_backend.model.Participant;
import com.bhumi.eventscoring_backend.model.Score;
import com.bhumi.eventscoring_backend.model.User;
import com.bhumi.eventscoring_backend.repository.ParticipantRepository;
import com.bhumi.eventscoring_backend.repository.ScoreRepository;
import com.bhumi.eventscoring_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.bhumi.eventscoring_backend.dto.LeaderboardEntry;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;
import com.bhumi.eventscoring_backend.repository.ParticipantRepository;
import com.bhumi.eventscoring_backend.model.Participant;

import java.util.Optional;

@RestController
@RequestMapping("/api/participants")
@CrossOrigin(origins = "http://localhost:5173")
public class ScoreController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ScoringService scoringService;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/{id}/scores")
    public Score submitScore(@PathVariable Long id,
                             @RequestBody ScoreRequest request,
                             Authentication authentication) {

        String judgeEmail = authentication.getName();

        User judge = userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals(judgeEmail))
                .findFirst()
                .orElseThrow();

        if (!judge.getRole().equals("JUDGE")) {
            throw new RuntimeException("Only judges can submit scores");
        }

        Optional<Participant> participantOpt = participantRepository.findById(id);

        if (participantOpt.isEmpty()) {
            throw new RuntimeException("Participant not found");
        }

        Score score = new Score();
        score.setParticipant(participantOpt.get());
        score.setJudge(judge);
        score.setCriteria(request.getCriteria());
        score.setValue(request.getValue());
        score.setRound(request.getRound());

        Score savedScore = scoreRepository.save(score);

        Long categoryId = participantOpt.get().getCategory().getId();
        broadcastLeaderboard(categoryId);

        return savedScore;
    }

    private void broadcastLeaderboard(Long categoryId) {
        List<Participant> participants = participantRepository.findAll().stream()
                .filter(p -> p.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());

        List<LeaderboardEntry> unranked = participants.stream()
                .map(p -> {
                    double finalScore = scoringService.calculateFinalScore(p.getId());
                    return new LeaderboardEntry(0, p.getUser().getName(), finalScore);
                })
                .sorted(Comparator.comparingDouble(LeaderboardEntry::getFinalScore).reversed())
                .collect(Collectors.toList());

        List<LeaderboardEntry> ranked = new java.util.ArrayList<>();
        int rank = 1;
        for (LeaderboardEntry entry : unranked) {
            ranked.add(new LeaderboardEntry(rank, entry.getParticipantName(), entry.getFinalScore()));
            rank++;
        }

        messagingTemplate.convertAndSend("/topic/leaderboard/" + categoryId, ranked);
    }
}