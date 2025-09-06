package ru.obninsk.net_safety_app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.obninsk.net_safety_app.dto.PasswordCheckRequestDto;
import ru.obninsk.net_safety_app.dto.PasswordCheckResponseDto;
import ru.obninsk.net_safety_app.service.PasswordCheckService;

@RestController
@RequestMapping("/passwordcheck")
@RequiredArgsConstructor
public class PasswordCheckController {
    private final PasswordCheckService passwordCheckService;

    @PostMapping("/check-password-strength")
    public ResponseEntity<PasswordCheckResponseDto> checkPasswordStrength(
            @Valid @RequestBody PasswordCheckRequestDto request){
        return ResponseEntity.ok(passwordCheckService.checkPasswordStrength(request));
    }

}
