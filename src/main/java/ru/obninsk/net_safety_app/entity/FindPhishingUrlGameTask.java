package ru.obninsk.net_safety_app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "find_phishing_url_game_task")
public class FindPhishingUrlGameTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = FindPhishingUrlGameTaskConverter.class)
    private Map<String, Boolean> urls;

    private String explanation;
}
