package ru.obninsk.net_safety_app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordCheckRequestDto {
    @NotBlank(message = "password field must not be blank")
    private String password;
}
