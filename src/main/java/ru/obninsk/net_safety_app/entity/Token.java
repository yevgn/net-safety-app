package ru.obninsk.net_safety_app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type")
    private TokenType tokenType;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_mode")
    private TokenMode tokenMode;

    private boolean expired = false;

    private boolean revoked = false;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;
}
