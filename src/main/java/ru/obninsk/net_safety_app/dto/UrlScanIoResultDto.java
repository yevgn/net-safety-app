package ru.obninsk.net_safety_app.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UrlScanIoResultDto {
    private Integer score;
    private List<String> categories;
    private String screenshotUrl;
    private String domUrl;
    private Integer totalLinks;
    private Integer maliciousLinks;
}
