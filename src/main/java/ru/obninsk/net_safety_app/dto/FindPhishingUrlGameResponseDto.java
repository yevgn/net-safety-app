package ru.obninsk.net_safety_app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FindPhishingUrlGameResponseDto {
    private Map<String, Boolean> urls;
    private String explanation;
}
