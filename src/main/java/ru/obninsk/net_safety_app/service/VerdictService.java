package ru.obninsk.net_safety_app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.obninsk.net_safety_app.entity.ResultCategory;

import java.util.List;

@Service
@Slf4j
public class VerdictService {
    private final Integer MIN_BOUND_FOR_HARMLESS = 5;

    public ResultCategory calculateOverallCategory(List<String> categories) {
        if (anyMaliciousOrPhishing(categories)) {
            return ResultCategory.MALICIOUS;
        } else if (anySuspicious(categories)) {
            return ResultCategory.SUSPICIOUS;
        } else if (countHarmless(categories) >= MIN_BOUND_FOR_HARMLESS) {
            return ResultCategory.HARMLESS;
        } else {
            return ResultCategory.UNDETECTED;
        }
    }

    public Float calculateConfidence(List<String> categories, ResultCategory verdict){
        if(verdict.equals(ResultCategory.MALICIOUS)){
            return (float) categories.size()/countHarmless(categories);
        } else if(verdict.equals(ResultCategory.SUSPICIOUS)){
            return (float) categories.size()/countSuspicious(categories);
        } else {
            return 100.0f;
        }
    }

    private boolean anyMaliciousOrPhishing(List<String> categories) {
        return categories
                .stream()
                .anyMatch(c ->
                        c.equalsIgnoreCase("malicious") || c.equalsIgnoreCase("phishing")
                );
    }

    private boolean anySuspicious(List<String> categories) {
        return categories
                .stream()
                .anyMatch(c -> c.equalsIgnoreCase("suspicious"));
    }

    private Long countHarmless(List<String> categories) {
        return categories
                .stream()
                .filter(c -> c.equalsIgnoreCase("harmless")).count();
    }

    private Long countMaliciousOrPhishing(List<String> categories){
        return categories
                .stream()
                .filter(c-> c.equalsIgnoreCase("malicious") || c.equalsIgnoreCase("phishing"))
                .count();
    }

    private Long countSuspicious(List<String> categories){
        return categories
                .stream()
                .filter(c-> c.equalsIgnoreCase("suspicious"))
                .count();
    }
}
