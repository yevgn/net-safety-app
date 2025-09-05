package ru.obninsk.net_safety_app.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.obninsk.net_safety_app.entity.Token;
import ru.obninsk.net_safety_app.entity.TokenMode;
import ru.obninsk.net_safety_app.entity.TokenType;
import ru.obninsk.net_safety_app.entity.User;
import ru.obninsk.net_safety_app.exception.InternalServerErrorException;
import ru.obninsk.net_safety_app.repository.TokenRepository;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {
    @Value("${spring.application.jwt.secret-key}")
    private String secretKey;
    @Value("${spring.application.jwt.user-access-token-expiration}")
    private Long userAccessTokenExpiration;
    @Value("${spring.application.jwt.user-refresh-token-expiration}")
    private Long userRefreshTokenExpiration;
    @Value("${spring.application.jwt.email-confirmation-token-expiration}")
    private Long emailConfirmationTokenExpiration;
    @Value("${spring.application.jwt.reset-2fa-token-expiration}")
    private Long reset2faTokenExpiration;
    @Value("${spring.application.name}")
    private String issuer;

    private final TokenRepository tokenRepository;

    public String extractUsername(String token) throws JwtException, IllegalArgumentException{
        return extractClaim(token, Claims::getSubject);
    }
    public String extractIssuedAt(String token) throws JwtException, IllegalArgumentException{
        return extractClaim(token, Claims::getIssuedAt).toString();
    }
    public String extractExpiredAt(String token) throws JwtException, IllegalArgumentException{
        return extractClaim(token, Claims::getExpiration).toString();
    }
    public String extractIssuer(String token) throws JwtException, IllegalArgumentException{
        return extractClaim(token, Claims::getIssuer);
    }
    public List<String> extractRoles(String token) throws JwtException, IllegalArgumentException {
        return extractClaim(token, claims -> claims.get("roles", List.class));
    }

    public String generateUserToken(
            List<String> roles,
            String email,
            TokenMode tokenMode
    ){
        Map<String, Object> extraClaims = new HashMap<>(Map.of("roles", roles));

        Long expiration = 0L;
        if(tokenMode.equals(TokenMode.ACCESS)){
            expiration = userAccessTokenExpiration;
        }
        else if(tokenMode.equals(TokenMode.REFRESH)){
            expiration = userRefreshTokenExpiration;
        }
        else if(tokenMode.equals(TokenMode.EMAIL_CONFIRMATION)){
            expiration = emailConfirmationTokenExpiration;
        } else if(tokenMode.equals(TokenMode.RESET_2FA)){
            expiration = reset2faTokenExpiration;
        }
        else{
            log.error(String.format("Не установлен срок годности токена mode: %s", tokenMode.name()));
            throw new InternalServerErrorException("Ошибка на сервере");
        }

        return buildToken(
                extraClaims,
                email,
                expiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            String email,
            long expiration
    ){
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .issuer(issuer)
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isTokenValidPriv(String token) throws JwtException, IllegalArgumentException{
            return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) throws JwtException, IllegalArgumentException {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) throws JwtException, IllegalArgumentException {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver)
            throws JwtException, IllegalArgumentException{
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) throws JwtException, IllegalArgumentException{
            return Jwts
                    .parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenValid(String token, TokenMode tokenMode) {
        boolean isValid = false;

        try {
            isValid = isTokenValidPriv(token);
        } catch (JwtException | IllegalArgumentException ex){
            return false;
        }

        Boolean isNotRevokedAndNotExpired = tokenRepository
                .findByToken(token)
                .filter(t -> t.getTokenMode().equals(tokenMode))
                .map(t -> !t.isRevoked() && !t.isExpired())
                .orElse(false);

        return isValid && isNotRevokedAndNotExpired;
    }

    Optional<User> findUserByToken(String token){
        return tokenRepository.findUserByToken(token);
    }

    public Token saveToken(String token, TokenType tokenType, TokenMode tokenMode, User user) {
        Token tokenEntity = Token.builder()
                .token(token)
                .tokenMode(tokenMode)
                .tokenType(tokenType)
                .user(user)
                .build();
        return tokenRepository.saveAndFlush(tokenEntity);
    }

    public int revokeUserTokensByTokenModeIn(String email, List<TokenMode> modes){
        return tokenRepository.revokeAllUsersTokensByTokenModeIn(email, modes);
    }

//    public int revokeAllUserTokens(String email){
//        return tokenRepository.revokeAllByUserEmail(email);
//    }
}
