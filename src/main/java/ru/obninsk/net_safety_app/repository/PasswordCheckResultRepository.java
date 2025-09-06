package ru.obninsk.net_safety_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.obninsk.net_safety_app.entity.PasswordCheckResult;

import java.util.Optional;

@Repository
public interface PasswordCheckResultRepository extends JpaRepository<PasswordCheckResult, Long> {
    Optional<PasswordCheckResult> findByPassword(String password);
}
