package ru.obninsk.net_safety_app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.obninsk.net_safety_app.dto.DtoFactory;
import ru.obninsk.net_safety_app.dto.PageResponseDto;
;
import ru.obninsk.net_safety_app.dto.UrlCheckShortResponseDto;
import ru.obninsk.net_safety_app.entity.ResultCategory;
import ru.obninsk.net_safety_app.entity.UrlCheckResult;
import ru.obninsk.net_safety_app.exception.InvalidPageParamsExeption;
import ru.obninsk.net_safety_app.repository.UrlCheckResultRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlCheckResultService {
    private final UrlCheckResultRepository urlCheckResultRepository;

    public UrlCheckResult save(UrlCheckResult entity){
        return urlCheckResultRepository.saveAndFlush(entity);
    }

    public PageResponseDto<UrlCheckShortResponseDto> findAllByCategoryOrderByMadeAt(
            ResultCategory category, Integer offset, Integer limit) {
        try {
            Pageable pageable = PageRequest.of(offset, limit, Sort.by("madeAt").descending());

            Page<UrlCheckResult> results;
            if(category != null) {
                 results = urlCheckResultRepository.findByOverallVerdictCategory(category, pageable);
            } else{
                results = urlCheckResultRepository.findAll(pageable);
            }

            return PageResponseDto.<UrlCheckShortResponseDto>builder()
                    .size(results.getSize())
                    .page(results.getNumber())
                    .hasNext(results.hasNext())
                    .hasPrevious(results.hasPrevious())
                    .totalPages(results.getTotalPages())
                    .totalElements(results.getTotalElements())
                    .content(
                            results.getContent().stream().map(DtoFactory::makeUrlCheckShortResponseDto).toList()
                    )
                    .build();
        } catch (IllegalArgumentException ex){
            throw new InvalidPageParamsExeption("Некорректные параметры offset или limit");
        }
    }
}
