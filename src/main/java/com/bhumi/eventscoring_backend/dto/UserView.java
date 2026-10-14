package com.bhumi.eventscoring_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserView {
    private Long id;
    private String name;
    private String email;
    private String role;
}