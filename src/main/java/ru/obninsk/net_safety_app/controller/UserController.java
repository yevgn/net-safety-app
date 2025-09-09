package ru.obninsk.net_safety_app.controller;


import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import ru.obninsk.net_safety_app.dto.UserResponseDto;

import ru.obninsk.net_safety_app.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
//@Tag(
//        name = "Контроллер для управления пользователями"
//)
public class UserController {
    private final UserService userService;

//    @GetMapping("/send-confirmation-message")
//    public ResponseEntity<?> sendEmailConfirmationMessage(@AuthenticationPrincipal User user)
//            throws MailSendException, MailAuthenticationException, MessagingException {
//        userService.sendConfirmationMessage(user);
//        return ResponseEntity.ok().build();
//     }

//    @Operation(
//            summary = "Подтверждение эл. почты",
//            description = "Ссылка отправляется на email пользователя вместе с токеном с целью подтверждения его эл. адреса"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "401", description = "Токен истек/неправильный"),
//            @ApiResponse(responseCode = "200", description = "ОК")
//    })
    @GetMapping("/confirm-email")
    public ResponseEntity<?> confirmEmail(@RequestParam("user_email") String email, @RequestParam("token") String token)
            throws MailSendException, MailAuthenticationException, MessagingException {
        userService.confirmEmail(email, token);
        return ResponseEntity.ok().build();
    }

//    @Operation(
//            summary = "Получение информации о пользователе"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "ОК")
//    })
    @GetMapping
    public ResponseEntity<UserResponseDto> findUser(){
        String principalEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.findUserDtoByEmail(principalEmail));
    }

//    @Operation(
//            summary = "Получение информации о всех пользователях"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "ОК")
//    })
    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDto>> findAll(){
        List<UserResponseDto> raw = userService.findAllUsersDto();
        raw.forEach(u -> {
                    u.setIsActivated(null);
                    u.setCreatedAt(null);
                    u.setEmail(null);
                    u.setIs2faEnabled(null);
                    u.setId(null);
                });
        return ResponseEntity.ok(raw);
    }
}