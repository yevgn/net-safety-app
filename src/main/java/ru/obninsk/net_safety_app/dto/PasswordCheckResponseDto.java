package ru.obninsk.net_safety_app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.obninsk.net_safety_app.entity.PasswordCategory;

import java.util.List;

@Data
@Builder
public class PasswordCheckResponseDto {
    private String password;
    @JsonProperty("is_leaked")
    private boolean isLeaked;
    @JsonProperty("password_category")
    private PasswordCategory passwordCategory;
    private List<String> suggestions;
}
