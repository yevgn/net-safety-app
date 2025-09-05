package ru.obninsk.net_safety_app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegistrationRequestDto {
    @NotBlank(message = "name field must not be empty")
    private String name;
    @NotBlank(message = "surname field must not be empty")
    private String surname;
    @NotNull(message = "email field must not be empty")
    @Pattern(
            regexp = "^[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)?@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Неправильный формат email"
    )
    private String email;

    @NotBlank(message = "password field must not be empty")
    private String password;

    @JsonProperty("is_2fa_enabled")
    private boolean is2faEnabled;
//    @JsonProperty("client_type")
//    @NotNull(message = "client_type field must not be empty")
//    private ClientType clientType;
}
