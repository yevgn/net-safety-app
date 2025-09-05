package ru.obninsk.net_safety_app.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;
import ru.obninsk.net_safety_app.dto.*;
import ru.obninsk.net_safety_app.entity.User;
import ru.obninsk.net_safety_app.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegistrationRequestDto request){
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/make-auth")
    public ResponseEntity<AuthResponseDto> authenticate(@Valid @RequestBody AuthRequestDto request){
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/verify-2fa-code")
    public ResponseEntity<AuthResponseDto> verify2faCode(@Valid @RequestBody VerificationRequestDto request){
        return ResponseEntity.ok(authService.verifyCode(request));
    }

    @PostMapping("/refresh-access-token")
    public ResponseEntity<AuthResponseDto> refreshAccessToken(@Valid @RequestBody RefreshAccessTokenRequestDto request){
        return ResponseEntity.ok(authService.refreshAccessToken(request));
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal User user){
        authService.logout(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-2fa-secret")
    public ResponseEntity<?> reset2faSecret(@Valid @RequestBody AuthRequestDto request)
            throws MailSendException, MailAuthenticationException, MessagingException {
        authService.reset2faSecret(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/enable-2fa")
    public ResponseEntity<AuthResponseDto> enable2fa(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(authService.enable2fa(user));
    }

    @GetMapping("/confirm-reset-2fa-secret")
    public ResponseEntity<AuthResponseDto> confirm2faSecretReset(
            @RequestParam("user_email") String email, @RequestParam("token") String token)
            throws MailSendException, MailAuthenticationException,MessagingException{

        authService.confirm2faSecretReset(email, token);
        return ResponseEntity.ok().build();
    }
}
