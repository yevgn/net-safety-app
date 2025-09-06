package ru.obninsk.net_safety_app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "url_check_results",
        uniqueConstraints = @UniqueConstraint(columnNames = {"url"}))
public class UrlCheckResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @Column(name = "made_at")
    private LocalDateTime madeAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_public")
    private boolean isPublic;

    @Column(name = "urlscanio_verdict", columnDefinition = "varchar")
    @Convert(converter = UrlCheckResultJsonConverter.class)
    private Map<String, Object> urlScanIoVerdict;

    @Column(name = "virustotal_verdict", columnDefinition = "varchar")
    @Convert(converter = UrlCheckResultJsonConverter.class)
    private Map<String, Object> virusTotalVerdict;

    @Column(name = "overall_verdict_category")
    @Enumerated(EnumType.STRING)
    private ResultCategory overallVerdictCategory;

    @Column(name = "confidence_percentage")
    private Float confidencePercentage;

    @Column(name = "screenshot_url")
    private String screenshotUrl;

    @Column(name = "dom_url")
    private String domUrl;

    @PrePersist
    private void configure(){
        madeAt = LocalDateTime.now();
    }

}
