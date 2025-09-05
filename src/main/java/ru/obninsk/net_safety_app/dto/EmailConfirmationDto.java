package ru.obninsk.net_safety_app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailConfirmationDto {
    @NotBlank(message = "email field must not be empty")
    private String email;
    @NotBlank(message = "token field must not be empty")
    private String token;
}
