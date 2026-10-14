package com.bhumi.eventscoring_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Participant participant;

    @ManyToOne
    private User judge;

    private String criteria;   // e.g. "Technique", "Expression"
    private Double value;      // e.g. 8.5
    private Integer round;     // e.g. 1, 2, 3
}
