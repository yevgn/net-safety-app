package ru.obninsk.net_safety_app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.obninsk.net_safety_app.dto.*;
import ru.obninsk.net_safety_app.entity.ResultCategory;
import ru.obninsk.net_safety_app.entity.UrlCheckResult;
import ru.obninsk.net_safety_app.entity.User;
import ru.obninsk.net_safety_app.repository.UrlCheckResultRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlCheckService {
    private final UrlCheckResultRepository urlCheckResultRepository;
    private final ServiceClient serviceClient;
    private final UrlCheckResultService urlCheckResultService;
    private final VerdictService verdictService;

    public UrlCheckResponseDto checkUrl(UrlCheckRequestDto request, User user) throws InterruptedException {
        String url = request.getUrl();
        Optional<UrlCheckResult> resultOpt = urlCheckResultRepository.findByUrl(url);
        UrlCheckResult entity;
        byte[] screenshot;

        if (resultOpt.isPresent()) {
            entity = resultOpt.get();
            String scUrl = entity.getScreenshotUrl();
            String scanId = scUrl
                    .substring(scUrl.lastIndexOf("/") + 1)
                    .replace(".png", "");

            try {
                screenshot = serviceClient.getScreenshotFromUrlScanIo(scanId);
            } catch (Exception ex) {
                String urlScanIoScanUuid = serviceClient.scanUrlOnUrlScanIo(url);
                Thread.sleep(20000L);
                UrlScanIoResultDto urlScanIoResult = serviceClient.getScanResultFromUrlScanIo(urlScanIoScanUuid);
                entity.setScreenshotUrl(urlScanIoResult.getScreenshotUrl());
                screenshot = serviceClient.getScreenshotFromUrlScanIo(urlScanIoScanUuid);
            }

        } else {
            String urlScanIoScanUuid = serviceClient.scanUrlOnUrlScanIo(url);
            Thread.sleep(20000L);
            UrlScanIoResultDto urlScanIoResult = serviceClient.getScanResultFromUrlScanIo(urlScanIoScanUuid);
            screenshot = serviceClient.getScreenshotFromUrlScanIo(urlScanIoScanUuid);

            String analysisId = serviceClient.scanUrlOnVirusTotal(url);
            Thread.sleep(15000L);
            VirusTotalResultDto virusTotalResult = serviceClient.getAnalysisFromVirusTotal(analysisId);

            List<String> categories = Stream.concat(
                            urlScanIoResult.getCategories().stream(),
                            virusTotalResult.getEnginesVerdicts().values().stream().map(o -> (String) o)
                    )
                    .toList();

            ResultCategory overallVerdict = verdictService.calculateOverallCategory(categories);
            Float confidence = verdictService.calculateConfidence(categories, overallVerdict);

            entity = UrlCheckResult
                    .builder()
                    .screenshotUrl(urlScanIoResult.getScreenshotUrl())
                    .domUrl(urlScanIoResult.getDomUrl())
                    .isPublic(request.getIsPublic())
                    .url(request.getUrl())
                    .urlScanIoVerdict(urlScanIoResultToMap(urlScanIoResult))
                    .virusTotalVerdict(virusTotalResult.getEnginesVerdicts())
                    .overallVerdictCategory(overallVerdict)
                    .user(user)
                    .confidencePercentage(confidence)
                    .build();
        }

        urlCheckResultService.save(entity);
        return DtoFactory.makeUrlCheckResponseDto(entity, screenshot, entity.getDomUrl());
    }


    private Map<String, Object> urlScanIoResultToMap(UrlScanIoResultDto result) {
        return Map.of(
                "score", result.getScore(),
                "categories", result.getCategories(),
                "totalLinks", result.getTotalLinks(),
                "maliciousLinks", result.getMaliciousLinks()
        );
    }

}
