package com.fitness.userservice.dto;

import com.fitness.userservice.models.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegisterRequest {

    @NotBlank(message = "Email required")
    @Email(message = "Not a valid password")
    private String email;
    @NotBlank(message = "Password required")
    @Size(min=7,message = "must be min 7 chars*")
    private String password;
    private String firstName;
    private String lastName;
    private String keyCloakId;
}
