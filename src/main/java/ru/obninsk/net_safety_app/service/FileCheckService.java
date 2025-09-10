package ru.obninsk.net_safety_app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.obninsk.net_safety_app.dto.DtoFactory;
import ru.obninsk.net_safety_app.dto.FileCheckResponseDto;
import ru.obninsk.net_safety_app.dto.VirusTotalResultDto;
import ru.obninsk.net_safety_app.entity.ResultCategory;
import ru.obninsk.net_safety_app.exception.FileTooLargeException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileCheckService {
    private final ServiceClient serviceClient;
    private final VerdictService verdictService;

    public FileCheckResponseDto checkFile(MultipartFile file) throws InterruptedException {
        double sizeInMb = file.getSize()/(1024.0 * 1024.0);

        if(sizeInMb>= 200){
            throw new FileTooLargeException(
                    String.format("Слишком большой размер файла: %s", sizeInMb)
            );
        }

        String analysisId = serviceClient
                .scanFile(file, sizeInMb >= 32 ? serviceClient.getUrlForLargeFileCheck() : "");
        Thread.sleep(20000L);
        VirusTotalResultDto checkResult = serviceClient.getAnalysisFromVirusTotal(analysisId);

        List<String> categories = checkResult.getEnginesVerdicts().values().stream().map(o -> (String) o).toList();

        ResultCategory overallCategory = verdictService.calculateOverallCategory(categories);
        Float confidence = verdictService.calculateConfidence(categories, overallCategory);

        return DtoFactory.makeFileCheckResponseDto(checkResult, overallCategory, confidence);
    }
}
