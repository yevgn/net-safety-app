package ru.obninsk.net_safety_app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.obninsk.net_safety_app.entity.ResultCategory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class UrlCheckResponseDto {
    private Integer score;
    private List<String> categories;
    @JsonProperty("total_links")
    private Integer totalLinks;
    @JsonProperty( "malicious_links")
    private Integer maliciousLinks;
    @JsonProperty("dom_url")
    private String domUrl;
    private byte[] image;
    @JsonProperty( "engines_verdicts")
    private Map<String, Object> enginesVerdicts;
    @JsonProperty( "overall_verdicts")
    private ResultCategory overallVerdict;
    @JsonProperty("confidence")
    private Float confidencePercentage;
    @JsonProperty( "user_info")
    private String userInfo;
    @JsonProperty("made_at")
    private LocalDateTime madeAt;
}
