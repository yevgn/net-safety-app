package ru.obninsk.net_safety_app.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.obninsk.net_safety_app.entity.ResultCategory;

import java.time.LocalDateTime;

@Data
@Builder
public class UrlCheckShortResponseDto {
    private String url;
    private ResultCategory category;
    private Float confidence;
    @JsonProperty("user_info")
    private String userInfo;
    @JsonManagedReference("scanned_at")
    private LocalDateTime scannedAt;
}
