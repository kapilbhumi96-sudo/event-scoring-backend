package com.bhumi.eventscoring_backend.dto;

import lombok.Data;

@Data
// automatically write the getter and setters
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String role;
}
// this is a temporary storage of the data given by the user and the dto
// (Data Transfer Object is just used to transfer it from one place to another)
// the temporary storage is used that the data entered by the user is not always in the format
// that is stored in the database . so we created a temporary space to avoid any security concern