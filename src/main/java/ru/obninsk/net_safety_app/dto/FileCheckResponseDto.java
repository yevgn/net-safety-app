package ru.obninsk.net_safety_app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.obninsk.net_safety_app.entity.ResultCategory;

import java.util.Map;

@Data
@Builder
public class FileCheckResponseDto {
    @JsonProperty("engines_verdicts")
    private Map<String, Object> enginesVerdicts;
    @JsonProperty("overall_category")
    private ResultCategory overallCategory;
    @JsonProperty("confidence")
    private Float confidencePercentage;
}
