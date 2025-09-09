package ru.obninsk.net_safety_app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import ru.obninsk.net_safety_app.entity.PasswordCheckResult;
import ru.obninsk.net_safety_app.repository.PasswordCheckResultRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordCheckResultService {
    private final PasswordCheckResultRepository passwordCheckResultRepository;

    Optional<PasswordCheckResult> findByPassword(String password){
        return passwordCheckResultRepository.findByPassword(password);
    }

    public PasswordCheckResult save(PasswordCheckResult entity) {
        return passwordCheckResultRepository.saveAndFlush(entity);
    }
}
