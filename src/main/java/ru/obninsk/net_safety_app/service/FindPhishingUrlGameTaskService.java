package ru.obninsk.net_safety_app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.obninsk.net_safety_app.entity.FindPhishingUrlGameTask;
import ru.obninsk.net_safety_app.repository.FindPhishingUrlGameTaskRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class FindPhishingUrlGameTaskService {
    private final FindPhishingUrlGameTaskRepository findPhishingUrlGameTaskRepository;

    public FindPhishingUrlGameTask save(FindPhishingUrlGameTask entity){
        return findPhishingUrlGameTaskRepository.saveAndFlush(entity);
    }
}
