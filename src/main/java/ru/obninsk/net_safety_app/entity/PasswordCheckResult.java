package ru.obninsk.net_safety_app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "password_check_results",
        uniqueConstraints = @UniqueConstraint(columnNames = {"password"}))
public class PasswordCheckResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String password;

    @Enumerated(EnumType.STRING)
    private PasswordCategory category;

    @Column(name = "is_leaked")
    private boolean isLeaked;

    @Column(name = "suggestions", columnDefinition = "varchar")
    @Convert(converter = PasswordCheckResultConverter.class)
    private List<String> suggestions;
}
