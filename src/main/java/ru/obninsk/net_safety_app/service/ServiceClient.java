package ru.obninsk.net_safety_app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import ru.obninsk.net_safety_app.dto.PasswordCheckResponseDto;
import ru.obninsk.net_safety_app.dto.UrlScanIoResultDto;
import ru.obninsk.net_safety_app.dto.VirusTotalResultDto;
import ru.obninsk.net_safety_app.entity.PasswordCategory;
import ru.obninsk.net_safety_app.exception.InternalServerErrorException;
import ru.obninsk.net_safety_app.exception.InvalidArgumentException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceClient {
    private final String urlScanIoApiKey = "01991109-26cf-7747-aac9-dff88a5c7fc2";
    private final String virusTotalApiKey = "e6ee051ceaa10a48a69472dfdd5463e4a5d6e4230f8ed389fc8527e3cfca590d";
    private final String stytchProjectId = "project-test-e07e47f7-1df6-43e7-bdc0-d834e740381e";
    private final String stytchApiKey = "secret-test-wnSY7pD4jC5tYMRozB8GtpE0RI7Q5xYm-Qs=";

    private final String scanUrlOnUrlScanIo = "https://urlscan.io/api/v1/scan";
    private final String getResultOnUrlScanIo = "https://urlscan.io/api/v1/result/{scanId}/";
    private final String getScreenshotOnUrlScanIo = "https://urlscan.io/screenshots/{scanId}.png";
    private final String getDomOnUrlScanIo = "https://urlscan.io/dom/{scanId}/";

    private final String getUrlForFileCheck = "https://www.virustotal.com/api/v3/files/upload_url";
    private final String fileCheckUrl = "https://www.virustotal.com/api/v3/files";

    private final String scanUrlOnVirusTotal = "https://www.virustotal.com/api/v3/urls";
    private final String getAnalysisFromVirusTotal = "https://www.virustotal.com/api/v3/analyses/{analysisId}";

    private final String checkPasswordStrengthOnStytch = "https://test.stytch.com/v1/passwords/strength_check";

    public String scanUrlOnUrlScanIo(String url){
        try {
            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("url", url);
            requestBody.put("visibility", "public");

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.set("api-key", urlScanIoApiKey);
            headers.set("Content-Type", "application/json");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(scanUrlOnUrlScanIo, request, Map.class);

            if(!response.getStatusCode().is2xxSuccessful()){
                throw new InternalServerErrorException("Ошибка на сервере");
            }

            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("uuid")) {
                throw new InternalServerErrorException("UUID не найден в ответе от UrlScanIo");
            }

            return (String) body.get("uuid");

        } catch (HttpClientErrorException ex){
            throw new InvalidArgumentException(ex.getMessage());
        } catch (HttpServerErrorException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public UrlScanIoResultDto getScanResultFromUrlScanIo(String scanId) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.set("api-key", urlScanIoApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>( headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    getResultOnUrlScanIo,
                    HttpMethod.GET,
                    request,
                    Map.class,
                    scanId
            );

            if(!response.getStatusCode().is2xxSuccessful()){
                throw new InternalServerErrorException("Ошибка на сервере");
            }

            Map<String, Object> body = response.getBody();
            if (body == null ) {
                throw new InternalServerErrorException("Пустой ответ от UrlScanIo");
            }

            Map<String, Object> verdicts = (Map<String, Object>) body.get("verdicts");
            Map<String, Object> overall = verdicts != null ? (Map<String, Object>) verdicts.get("overall") : null;

            Integer score = overall != null ? (Integer) overall.get("score") : null;
            List<String> categories = overall != null ? (List<String>) overall.get("categories") : Collections.emptyList();

            Map<String, Object> task = (Map<String, Object>) body.get("task");
            String screenshotUrl = task != null ? (String) task.get("screenshotURL") : null;
            String domUrl = task != null ? (String) task.get("domURL") : null;

            Map<String, Object> stats = (Map<String, Object>) body.get("stats");
            Integer totalLinks = stats != null ? (Integer) stats.get("totalLinks") : null;
            Integer malicious = stats != null ? (Integer) stats.get("malicious") : null;

            return UrlScanIoResultDto
                    .builder()
                    .score(score)
                    .categories(categories)
                    .totalLinks(totalLinks)
                    .maliciousLinks(malicious)
                    .screenshotUrl(screenshotUrl)
                    .domUrl(domUrl)
                    .build();

        } catch (HttpClientErrorException ex){
            throw new InvalidArgumentException(ex.getMessage());
        } catch (HttpServerErrorException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    public byte[] getScreenshotFromUrlScanIo(String scanId) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.set("api-key", urlScanIoApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>( headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(
                    getScreenshotOnUrlScanIo,
                    HttpMethod.GET,
                    request,
                    byte[].class,
                    scanId
            );

            if(!response.getStatusCode().is2xxSuccessful()){
                throw new InternalServerErrorException("Ошибка на сервере");
            }

            byte[] image = response.getBody();
            if (image == null || image.length == 0 ) {
                throw new InternalServerErrorException("Пустой ответ от UrlScanIo");
            }
            return image;

        } catch (HttpClientErrorException ex){
            throw new InvalidArgumentException(ex.getMessage());
        } catch (HttpServerErrorException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    public String getDomFromUrlScanIo(String scanId) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.set("api-key", urlScanIoApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>( headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    getDomOnUrlScanIo,
                    HttpMethod.GET,
                    request,
                    String.class,
                    scanId
            );

            if(!response.getStatusCode().is2xxSuccessful()){
                throw new InternalServerErrorException("Ошибка на сервере");
            }

            String dom = response.getBody();
            if (dom == null || dom.isBlank() ) {
                throw new InternalServerErrorException("Пустой ответ от UrlScanIo");
            }
            return dom;

        } catch (HttpClientErrorException ex){
            throw new InvalidArgumentException(ex.getMessage());
        } catch (HttpServerErrorException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public String scanUrlOnVirusTotal(String url) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("url", url);

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.set("x-apikey", virusTotalApiKey);
            headers.set("content-type", "application/x-www-form-urlencoded");
            headers.set("accept", "application/json");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(scanUrlOnVirusTotal, request, Map.class);

            if(!response.getStatusCode().is2xxSuccessful()){
                throw new InternalServerErrorException("Ошибка на сервере");
            }

            Map<String, Object> body = response.getBody();
            if (body == null) {
                throw new InternalServerErrorException("Пустой ответ от virus total");
            }

            Map<String, Object> data = ( Map<String, Object> ) body.get("data");

            if(data == null || !data.containsKey("id")){
                throw new InternalServerErrorException("Пустой ответ от virus total");
            }

            return (String) data.get("id");

        } catch (HttpClientErrorException ex){
            throw new InvalidArgumentException(ex.getMessage());
        } catch (HttpServerErrorException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public VirusTotalResultDto getAnalysisFromVirusTotal(String analysisId) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.set("x-apikey", virusTotalApiKey);
            headers.set("accept", "application/json");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>( headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    getAnalysisFromVirusTotal,
                    HttpMethod.GET,
                    request,
                    Map.class,
                    analysisId
            );

            if(!response.getStatusCode().is2xxSuccessful()){
                throw new InternalServerErrorException("Ошибка на сервере");
            }

            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("data")) {
                throw new InternalServerErrorException("Пустой ответ от virus total");
            }

            Map<String, Object> data = (Map<String, Object>) body.get("data");
            Map<String, Object> attributes =  (Map<String, Object>) data.get("attributes");
            Map<String, Object> results = attributes != null ? (Map<String, Object>) attributes.get("results") : null;

            if (results != null) {
                Map<String, Object> engineVerdicts = new HashMap<>();
                results.entrySet().stream()
                        .limit(10)
                        .forEach(entry -> {
                            String engineKey = entry.getKey();
                            Map<String, Object> engineData = (Map<String, Object>) entry.getValue();

                            if(engineData.containsKey("engine_name") && engineData.containsKey("category")) {
                                String engineName = (String) engineData.get("engine_name");
                                String category = (String) engineData.get("category");
                                engineVerdicts.put(engineName, category);
                            }
                        });
                return VirusTotalResultDto
                        .builder()
                        .enginesVerdicts(engineVerdicts)
                        .build();
            }

            throw new InternalServerErrorException("Пустой ответ от virus total");

        } catch (HttpClientErrorException ex){
            throw new InvalidArgumentException(ex.getMessage());
        } catch (HttpServerErrorException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    public String getUrlForLargeFileCheck() {
        try {
            RestTemplate restTemplate = new RestTemplate();

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.set("x-apikey", virusTotalApiKey);
            headers.set("accept", "application/json");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>( headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    getUrlForFileCheck,
                    HttpMethod.GET,
                    request,
                    Map.class
            );

            if(!response.getStatusCode().is2xxSuccessful()){
                throw new InternalServerErrorException("Ошибка на сервере");
            }

            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("data")) {
                throw new InternalServerErrorException("Пустой ответ от virus total");
            }

            return (String) body.get("data");

        } catch (HttpClientErrorException ex){
            throw new InvalidArgumentException(ex.getMessage());
        } catch (HttpServerErrorException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public PasswordCheckResponseDto checkPasswordStrength(String password) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("password", password);

            String auth = stytchProjectId + ":" + stytchApiKey;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.set("content-type", "application/json");
            headers.set("Authorization", "Basic " + encodedAuth);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(checkPasswordStrengthOnStytch, request, Map.class);

            if(!response.getStatusCode().is2xxSuccessful()){
                throw new InternalServerErrorException("Ошибка на сервере");
            }

            Map<String, Object> body = response.getBody();
            if (body == null) {
                throw new InternalServerErrorException("Пустой ответ от Stytch");
            }

            Boolean isLeaked = (Boolean) body.get("breached_password");
            Integer score = (Integer) body.get("score");

            PasswordCategory category;
            if(score == null){
                category = PasswordCategory.UNKNOWN;
            } else {
                category = switch (score) {
                    case 0 -> PasswordCategory.TOO_WEAK;
                    case 1 -> PasswordCategory.TOO_WEAK;
                    case 2 -> PasswordCategory.WEAK;
                    case 3 -> PasswordCategory.NORMAL;
                    case 4 -> PasswordCategory.STRONG;
                    default -> PasswordCategory.UNKNOWN;
                };
            }

            Map<String, Object> feedback = (Map<String, Object>) body.get("feedback");
            List<String> suggestions = feedback == null ? List.of() : (List<String>) feedback.get("suggestions");

            return PasswordCheckResponseDto
                    .builder()
                    .password(password)
                    .isLeaked(isLeaked)
                    .passwordCategory(category)
                    .suggestions(suggestions)
                    .build();

        } catch (HttpClientErrorException ex){
            throw new InvalidArgumentException(ex.getMessage());
        } catch (HttpServerErrorException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    public String scanFile(MultipartFile file, String largeFileCheckUrl) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
            requestBody.add(
                    "file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename())
            );

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.set("x-apikey", virusTotalApiKey);
            headers.set("content-type", "multipart/form-data");
            headers.set("accept", "application/json");

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(requestBody, headers);

            String url = largeFileCheckUrl == null || largeFileCheckUrl.isEmpty() ? fileCheckUrl : largeFileCheckUrl;
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if(!response.getStatusCode().is2xxSuccessful()){
                throw new InternalServerErrorException("Ошибка на сервере");
            }

            Map<String, Object> body = response.getBody();
            if (body == null) {
                throw new InternalServerErrorException("Пустой ответ от virus total");
            }

            Map<String, Object> data = ( Map<String, Object> ) body.get("data");

            if(data == null || !data.containsKey("id")){
                throw new InternalServerErrorException("Пустой ответ от virus total");
            }

            return (String) data.get("id");

        } catch (HttpClientErrorException ex){
            throw new InvalidArgumentException(ex.getMessage());
        } catch (HttpServerErrorException | IOException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }
//
//    public List<String> translateToRussian(List<String> suggestions) {
//
//    }

    private static class MultipartInputStreamFileResource extends InputStreamResource {

        private final String filename;

        public MultipartInputStreamFileResource(InputStream inputStream, String filename) {
            super(inputStream);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }

        @Override
        public long contentLength() throws IOException {
            return -1;
        }
    }

}
