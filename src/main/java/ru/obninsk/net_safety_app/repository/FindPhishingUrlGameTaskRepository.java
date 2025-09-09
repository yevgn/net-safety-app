package ru.obninsk.net_safety_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.obninsk.net_safety_app.entity.FindPhishingUrlGameTask;

@Repository
public interface FindPhishingUrlGameTaskRepository extends JpaRepository<FindPhishingUrlGameTask, Long> {
}
