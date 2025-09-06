package ru.obninsk.net_safety_app.service;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.obninsk.net_safety_app.dto.*;
import ru.obninsk.net_safety_app.entity.Role;
import ru.obninsk.net_safety_app.entity.TokenMode;
import ru.obninsk.net_safety_app.entity.TokenType;
import ru.obninsk.net_safety_app.entity.User;
import ru.obninsk.net_safety_app.exception.*;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserService userService;
    private final TwoFactorAuthenticationService twoFactorAuthenticationService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final EmailService emailService;

    @Transactional(rollbackFor = Exception.class)
    public AuthResponseDto register(RegistrationRequestDto request) throws MessagingException {
        if (userService.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyRegisteredException(
                    String.format("Пользователь с таким email уже зарегистрирован: %s", request.getEmail())
            );
        }

        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .surname(request.getSurname())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        if (request.is2faEnabled()) {
            user.setSecret(twoFactorAuthenticationService.generateNewSecret());
            user.set2faEnabled(true);
        }

        userService.save(user);
        userService.sendConfirmationMessage(user);

        String accessToken = tokenService.generateUserToken(List.of(Role.USER.name()), user.getEmail(), TokenMode.ACCESS);
        String refreshToken = tokenService.generateUserToken(List.of(Role.USER.name()), user.getEmail(), TokenMode.REFRESH);

        tokenService.saveToken(accessToken, TokenType.BEARER, TokenMode.ACCESS, user);
        tokenService.saveToken(refreshToken, TokenType.BEARER, TokenMode.REFRESH, user);

        return AuthResponseDto
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .is2faEnabled(user.is2faEnabled())
                .secretImageUri(
                        request.is2faEnabled() ?
                                twoFactorAuthenticationService.generateQrCodeImageUri(user.getSecret(), user.getEmail()): null)
                .secret(user.getSecret())
                .build();
    }

    public AuthResponseDto authenticate(AuthRequestDto request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Пользователь с email %s не найден", request.getEmail())
                ));

        if(!user.isActivated()){
            throw new UserNotActivatedException(
                    String.format("Пользователь %s не подтвердил адрес эл. почты", user.getEmail())
            );
        }

        if (user.is2faEnabled()) {
            return AuthResponseDto.builder()
                    .accessToken("")
                    .refreshToken("")
                    .is2faEnabled(true)
                    .build();
        }

        tokenService.revokeUserTokensByTokenModeIn(user.getEmail(), List.of(TokenMode.ACCESS, TokenMode.REFRESH));
        var accessToken = tokenService.generateUserToken(List.of(Role.USER.name()), user.getEmail(), TokenMode.ACCESS);
        var refreshToken = tokenService.generateUserToken(List.of(Role.USER.name()), user.getEmail(), TokenMode.REFRESH);
        tokenService.saveToken(accessToken, TokenType.BEARER, TokenMode.ACCESS, user);
        tokenService.saveToken(refreshToken, TokenType.BEARER, TokenMode.REFRESH, user);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .is2faEnabled(false)
                .build();
    }

    public AuthResponseDto verifyCode(VerificationRequestDto request) {
        User user = userService
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Пользователь с email %s не найден", request.getEmail()))
                );

        if (twoFactorAuthenticationService.isOtpNotValid(user.getSecret(), request.getCode())) {
            throw new BadCredentialsException(
                    String.format("Неправильный код 2FA пользователя %s", user.getEmail())
            );
        }

        var accessToken = tokenService.generateUserToken(List.of(Role.USER.name()), user.getEmail(), TokenMode.ACCESS);
        var refreshToken = tokenService.generateUserToken(List.of(Role.USER.name()), user.getEmail(), TokenMode.REFRESH);
        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .is2faEnabled(user.is2faEnabled())
                .build();
    }

    public AuthResponseDto refreshAccessToken(RefreshAccessTokenRequestDto request) {
        String refreshToken = request.getRefreshToken();
        if (!tokenService.isTokenValid(refreshToken, TokenMode.REFRESH)) {
            throw new TokenValidationFailureException(
                    String.format("refresh Токен не прошел валидацию: %s", refreshToken)
            );
        }

        User user = tokenService.findUserByToken(refreshToken)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Пользователь по токену %s не найден", refreshToken)
                ));

        tokenService.revokeUserTokensByTokenModeIn(user.getEmail(), List.of(TokenMode.ACCESS, TokenMode.REFRESH));
        String accessToken = tokenService.generateUserToken(List.of(Role.USER.name()), user.getEmail(), TokenMode.ACCESS);
        String newRefreshToken = tokenService.generateUserToken(List.of(Role.USER.name()), user.getEmail(), TokenMode.REFRESH);

        tokenService.saveToken(accessToken, TokenType.BEARER, TokenMode.ACCESS, user);
        tokenService.saveToken(newRefreshToken, TokenType.BEARER, TokenMode.REFRESH, user);

        return AuthResponseDto
                .builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .is2faEnabled(user.is2faEnabled())
                .build();
    }

    public void logout(User user) {
        tokenService.revokeUserTokensByTokenModeIn(user.getEmail(), List.of(TokenMode.ACCESS, TokenMode.REFRESH));
    }

    public void reset2faSecret(AuthRequestDto request) throws MailSendException, MailAuthenticationException, MessagingException {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Пользователь с email %s не найден", request.getEmail()))
                );

        if(!user.isActivated()){
            throw new EmailNotConfirmedException("Для того чтобы подключить 2FA, необходимо подтвердить адрес эл. почты");
        }

        emailService.send2faResetUriMessage(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void confirm2faSecretReset(String email, String token)
            throws MailSendException, MailAuthenticationException, MessagingException {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Пользователь с %s email не найден", email)
                ));

        if (!tokenService.isTokenValid(token, TokenMode.RESET_2FA)) {
            emailService.send2faResetUriMessage(user);
            throw new Reset2faSecretTokenRenewException(
                    String.format("RESET_2FA_TOKEN пользователя %s истек. Произошла выдача нового токена", email)
            );
        }

        User tokenUser = tokenService.findUserByToken(token)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Пользователь по токену %s не найден", token)
                ));

        if (!tokenUser.equals(user)) {
            throw new DataMismatchException("Указанный email не совпадает с email владельца токена");
        }

        user.setSecret(null);
        user.set2faEnabled(false);
        userService.save(user);

        emailService.send2faSecretResetNotification(user);
    }

    @Transactional
    public AuthResponseDto enable2fa(User user) {
        if (user.is2faEnabled()) {
            throw new TfaAlreadyEnabledException(
                    String.format("2FA для пользователя %s уже включена", user.getEmail())
            );
        }

        user.setSecret(twoFactorAuthenticationService.generateNewSecret());
        user.set2faEnabled(true);
        userService.save(user);

        tokenService.revokeUserTokensByTokenModeIn(user.getEmail(), List.of(TokenMode.ACCESS, TokenMode.REFRESH));
        String accessToken = tokenService.generateUserToken(List.of(Role.USER.name()), user.getEmail(), TokenMode.ACCESS);
        String refreshToken = tokenService.generateUserToken(List.of(Role.USER.name()), user.getEmail(), TokenMode.REFRESH);

        tokenService.saveToken(accessToken, TokenType.BEARER, TokenMode.ACCESS, user);
        tokenService.saveToken(refreshToken, TokenType.BEARER, TokenMode.REFRESH, user);

        return AuthResponseDto
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .is2faEnabled(user.is2faEnabled())
                .secretImageUri(twoFactorAuthenticationService.generateQrCodeImageUri(user.getSecret(), user.getEmail()))
                .secret(user.getSecret())
                .build();
    }
}

