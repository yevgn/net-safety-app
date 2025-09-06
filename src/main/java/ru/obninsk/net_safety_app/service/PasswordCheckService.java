package ru.obninsk.net_safety_app.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.obninsk.net_safety_app.dto.PasswordCheckRequestDto;
import ru.obninsk.net_safety_app.dto.PasswordCheckResponseDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordCheckService {

    public PasswordCheckResponseDto checkPasswordStrength( PasswordCheckRequestDto request) {
        return null;
    }
}
