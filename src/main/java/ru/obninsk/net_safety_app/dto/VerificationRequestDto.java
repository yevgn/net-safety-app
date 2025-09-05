package ru.obninsk.net_safety_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerificationRequestDto {
    @NotBlank(message = "email field must not be empty")
    @Pattern(
            regexp = "^[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)?@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Неправильный формат email"
    )
    private String email;

    @NotBlank(message = "code field must not be empty")
    private String code;
}
