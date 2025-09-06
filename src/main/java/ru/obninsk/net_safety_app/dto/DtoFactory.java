package ru.obninsk.net_safety_app.dto;

import ru.obninsk.net_safety_app.entity.PasswordCheckResult;
import ru.obninsk.net_safety_app.entity.ResultCategory;
import ru.obninsk.net_safety_app.entity.UrlCheckResult;
import ru.obninsk.net_safety_app.entity.User;

import java.util.List;
import java.util.Map;

public class DtoFactory {
    public static UserResponseDto makeUserResponseDto(User user){
        return UserResponseDto
                .builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .is2faEnabled(user.is2faEnabled())
                .isActivated(user.isActivated())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @SuppressWarnings("unchecked")
    public static UrlCheckResponseDto makeUrlCheckResponseDto(UrlCheckResult result, byte[] screenshot, String domUrl){
        Map<String, Object> urlScanIoVerdict = result.getUrlScanIoVerdict();
        Map<String, Object> virusTotalVerdict = result.getVirusTotalVerdict();
        return UrlCheckResponseDto
                .builder()
                .score((Integer) urlScanIoVerdict.get("score"))
                .categories((List<String>) urlScanIoVerdict.get("categories"))
                .image(screenshot)
                .userInfo(result.getUser().getName() + " " + result.getUser().getSurname())
                .overallVerdict(result.getOverallVerdictCategory())
                .domUrl(domUrl)
                .maliciousLinks((Integer) urlScanIoVerdict.get("maliciousLinks"))
                .totalLinks((Integer) urlScanIoVerdict.get("totalLinks"))
                .madeAt(result.getMadeAt())
                .enginesVerdicts(virusTotalVerdict)
                .confidencePercentage(result.getConfidencePercentage())
                .build();
    }

    public static FileCheckResponseDto makeFileCheckResponseDto(VirusTotalResultDto res, ResultCategory category, Float conf){
        return FileCheckResponseDto
                .builder()
                .enginesVerdicts(res.getEnginesVerdicts())
                .overallCategory(category)
                .confidencePercentage(conf)
                .build();
    }

    public static UrlCheckShortResponseDto makeUrlCheckShortResponseDto(UrlCheckResult result){
        return UrlCheckShortResponseDto
                .builder()
                .url(result.getUrl())
                .category(result.getOverallVerdictCategory())
                .confidence(result.getConfidencePercentage())
                .scannedAt(result.getMadeAt())
                .userInfo(
                        result.isPublic() ? result.getUser().getName() + " " + result.getUser().getSurname() : null
                )
                .build();
    }

    public static PasswordCheckResponseDto makePasswordCheckResponseDto(PasswordCheckResult passwordCheckResult) {
        return PasswordCheckResponseDto
                .builder()
                .password(passwordCheckResult.getPassword())
                .passwordCategory(passwordCheckResult.getCategory())
                .isLeaked(passwordCheckResult.isLeaked())
                .suggestions(passwordCheckResult.getSuggestions())
                .build();
    }
}
