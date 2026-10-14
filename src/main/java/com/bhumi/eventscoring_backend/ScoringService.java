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
public class ScoringService {

    @Autowired
    private ScoreRepository scoreRepository;

    public double calculateFinalScore(Long participantId) {
        List<Score> allScores = scoreRepository.findAll().stream()
                .filter(s -> s.getParticipant().getId().equals(participantId))
                .collect(Collectors.toList());

        if (allScores.isEmpty()) {
            return 0.0;
        }

        List<Double> judgeAverages = groupAndAverageByJudge(allScores);

        if (judgeAverages.size() >= 3) {
            judgeAverages.remove(Collections_max(judgeAverages));
            judgeAverages.remove(Collections_min(judgeAverages));
        }

        return judgeAverages.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    public double getHighestSingleRoundScore(Long participantId) {
        return scoreRepository.findAll().stream()
                .filter(s -> s.getParticipant().getId().equals(participantId))
                .mapToDouble(Score::getValue)
                .max()
                .orElse(0.0);
    }

    private List<Double> groupAndAverageByJudge(List<Score> scores) {
        Map<Long, List<Score>> byJudge = scores.stream()
                .collect(Collectors.groupingBy(s -> s.getJudge().getId()));

        return byJudge.values().stream()
                .map(judgeScores -> judgeScores.stream()
                        .mapToDouble(Score::getValue)
                        .average()
                        .orElse(0.0))
                .collect(Collectors.toList());
    }

    private Double Collections_max(List<Double> list) {
        return list.stream().max(Comparator.naturalOrder()).orElse(0.0);
    }

    private Double Collections_min(List<Double> list) {
        return list.stream().min(Comparator.naturalOrder()).orElse(0.0);
    }
}