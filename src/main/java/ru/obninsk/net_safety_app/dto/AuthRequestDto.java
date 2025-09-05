package ru.obninsk.net_safety_app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthRequestDto {
    @NotBlank(message = "email field must not be empty")
    private String email;
    @NotBlank(message = "password field must not be empty")
    private String password;
//
//    @JsonProperty("client_type")
//    @NotNull(message = "client_type field must not be empty")
//    private ClientType clientType;
}
