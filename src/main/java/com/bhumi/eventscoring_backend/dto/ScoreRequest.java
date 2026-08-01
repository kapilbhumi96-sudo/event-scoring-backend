package com.bhumi.eventscoring_backend.dto;

import lombok.Data;

@Data
public class ScoreRequest {
    private String criteria;
    private Double value;
    private Integer round;
}