package ru.obninsk.net_safety_app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.obninsk.net_safety_app.dto.DtoFactory;
import ru.obninsk.net_safety_app.dto.PasswordCheckRequestDto;
import ru.obninsk.net_safety_app.dto.PasswordCheckResponseDto;
import ru.obninsk.net_safety_app.entity.PasswordCheckResult;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordCheckService {
    private final ServiceClient serviceClient;
    private final PasswordCheckResultService passwordCheckResultService;

    public PasswordCheckResponseDto checkPasswordStrength( PasswordCheckRequestDto request) {
        String password = request.getPassword();
        Optional<PasswordCheckResult> opt = passwordCheckResultService.findByPassword(password);
        if(opt.isPresent()) {
            return DtoFactory.makePasswordCheckResponseDto(opt.get());
        }

        PasswordCheckResponseDto result = serviceClient.checkPasswordStrength(password);
        PasswordCheckResult entity = PasswordCheckResult
                .builder()
                .password(result.getPassword())
                .category(result.getPasswordCategory())
                .isLeaked(result.isLeaked())
                .suggestions(result.getSuggestions() == null ? List.of() : result.getSuggestions())
                .build();
        passwordCheckResultService.save(entity);
        return result;
    }
}
