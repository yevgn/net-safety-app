package ru.obninsk.net_safety_app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.obninsk.net_safety_app.entity.ResultCategory;
import ru.obninsk.net_safety_app.entity.UrlCheckResult;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlCheckResultRepository extends JpaRepository<UrlCheckResult, Long> {
    Optional<UrlCheckResult> findByUrl(String url);

    Page<UrlCheckResult> findByOverallVerdictCategory(ResultCategory overallVerdictCategory, Pageable pageable);
}
