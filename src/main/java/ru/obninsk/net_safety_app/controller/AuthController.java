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
//@Tag(
//        name = "Контроллер, предоставляющий возможности регистрации и аутентификации"
//)
public class AuthController {
    private final AuthService authService;

//    @Operation(
//            summary = "Регистрация аккаунта"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "400", description = "Введены некорректные данные/данные пропущены",
//                    content = @Content(mediaType = "application/json")),
//            @ApiResponse(responseCode = "200", description = "ОК")
//    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegistrationRequestDto request) throws MessagingException {
        return ResponseEntity.ok(authService.register(request));
    }

//    @Operation(
//            summary = "Аутентификация",
//            description = "Получение access token и refresh token для использования защищенных ресурсов"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "401", description = "Неправильный логин/пароль",
//                    content = @Content(mediaType = "application/json")),
//            @ApiResponse(responseCode = "200", description = "ОК")
//    })
    @PostMapping("/make-auth")
    public ResponseEntity<AuthResponseDto> authenticate(@Valid @RequestBody AuthRequestDto request){
        return ResponseEntity.ok(authService.authenticate(request));
    }

//    @Operation(
//            summary = "Верификация 2FA code",
//            description = "Используется, только если включена 2FA"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "401", description = "Неправильный код",
//                    content = @Content(mediaType = "application/json")),
//            @ApiResponse(responseCode = "200", description = "ОК")
//    })
    @PostMapping("/verify-2fa-code")
    public ResponseEntity<AuthResponseDto> verify2faCode(@Valid @RequestBody VerificationRequestDto request){
        return ResponseEntity.ok(authService.verifyCode(request));
    }

//    @Operation(
//            summary = "Обновление access token"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "401", description = "Refresh token истек/неправильный",
//                    content = @Content(mediaType = "application/json")),
//            @ApiResponse(responseCode = "200", description = "ОК")
//    })
    @PostMapping("/refresh-access-token")
    public ResponseEntity<AuthResponseDto> refreshAccessToken(@Valid @RequestBody RefreshAccessTokenRequestDto request){
        return ResponseEntity.ok(authService.refreshAccessToken(request));
    }

//    @Operation(
//            summary = "Выход из системы"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "ОК")
//    })
    @GetMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal User user){
        authService.logout(user);
        return ResponseEntity.ok().build();
    }

//    @Operation(
//            summary = "Сброс 2FA secret",
//            description = "Используется, если пользователь, например, утратил доступ к уйстройству с 2FA secret"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "401", description = "Неправильные данные",
//                    content = @Content(mediaType = "application/json")),
//            @ApiResponse(responseCode = "200", description = "ОК")
//    })
    @PostMapping("/reset-2fa-secret")
    public ResponseEntity<?> reset2faSecret(@Valid @RequestBody AuthRequestDto request)
            throws MailSendException, MailAuthenticationException, MessagingException {
        authService.reset2faSecret(request);
        return ResponseEntity.ok().build();
    }

//    @Operation(
//            summary = "Включение 2FA"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "ОК")
//    })
    @GetMapping("/enable-2fa")
    public ResponseEntity<AuthResponseDto> enable2fa(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(authService.enable2fa(user));
    }

//
    @GetMapping("/confirm-reset-2fa-secret")
    public ResponseEntity<AuthResponseDto> confirm2faSecretReset(
            @RequestParam("user_email") String email, @RequestParam("token") String token)
            throws MailSendException, MailAuthenticationException,MessagingException{

        authService.confirm2faSecretReset(email, token);
        return ResponseEntity.ok().build();
    }
}
