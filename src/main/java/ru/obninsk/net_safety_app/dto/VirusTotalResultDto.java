package ru.obninsk.net_safety_app.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class VirusTotalResultDto {
    Map<String, Object> enginesVerdicts;
}
