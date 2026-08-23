package com.bhumi.eventscoring_backend;

import com.bhumi.eventscoring_backend.model.Score;
import com.bhumi.eventscoring_backend.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
// Marks this class as the business-logic layer where the scoring rules/calculations are implemented.
public class ScoringService {
// Spring automatically provides the repository object so this service can access score data from the database.

    @Autowired
    private ScoreRepository scoreRepository;

    public double calculateFinalScore(Long participantId) {
        // Main method: calculates and returns one final score for a participant.

        List<Score> allScores = scoreRepository.findAll().stream()
                .filter(s -> s.getParticipant().getId().equals(participantId))
                .collect(Collectors.toList());
        // Get all scores → keep only scores belonging to this participant → convert the Stream back into a List.


        if (allScores.isEmpty()) {
            return 0.0;
        }

        List<Double> judgeAverages = groupAndAverageByJudge(allScores);
        // Groups the participant's scores by judge and calculates one average score for each judge.

        if (judgeAverages.size() >= 3) {
            judgeAverages.remove(Collections_max(judgeAverages));
            judgeAverages.remove(Collections_min(judgeAverages));
        }
        // Apply the drop-highest and drop-lowest rule only when there are at least 3 judges.


        return judgeAverages.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }
    // Convert Double objects to primitive doubles → calculate the average of the remaining judge scores
    // → return 0 if no value exists.

    public double getHighestSingleRoundScore(Long participantId) {
        // Returns the highest individual raw score received by the participant, this is used only to break a tie.

        return scoreRepository.findAll().stream()
                .filter(s -> s.getParticipant().getId().equals(participantId))
                .mapToDouble(Score::getValue)
                .max()
                .orElse(0.0);
    }
    // Get this participant's raw scores → extract their numerical values →
    // find the highest one → return 0.0 if none exists.

    private List<Double> groupAndAverageByJudge(List<Score> scores) {
        // Converts many raw score records into one average score for each judge.

        Map<Long, List<Score>> byJudge = scores.stream()
                .collect(Collectors.groupingBy(s -> s.getJudge().getId()));
        // Groups Score objects into buckets using judge ID as the key: Judge 1 → scores, Judge 2 → scores, etc.

        return byJudge.values().stream()
                .map(judgeScores -> judgeScores.stream()
                        .mapToDouble(Score::getValue)
                        .average()
                        .orElse(0.0))
                .collect(Collectors.toList());
    }
    // For each judge: extract score values →
    // calculate that judge's average → collect all judge averages into a List.

    private Double Collections_max(List<Double> list) {
        return list.stream().max(Comparator.naturalOrder()).orElse(0.0);
    }
    // Finds the largest value in the list; returns 0.0 if the list is empty.

    private Double Collections_min(List<Double> list) {
        return list.stream().min(Comparator.naturalOrder()).orElse(0.0);
    }
    // Finds the smallest value in the list; returns 0.0 if the list is empty.
}