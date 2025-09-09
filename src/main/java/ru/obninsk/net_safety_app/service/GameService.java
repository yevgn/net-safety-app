package ru.obninsk.net_safety_app.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.obninsk.net_safety_app.dto.FindPhishingUrlGameResponseDto;
import ru.obninsk.net_safety_app.dto.GigaChatRequestDto;

import ru.obninsk.net_safety_app.entity.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class GameService {
    private final ServiceClient serviceClient;
    private final TokenService tokenService;
    private final UserService userService;
    private final FindPhishingUrlGameTaskService findPhishingUrlGameTaskService;

    public FindPhishingUrlGameResponseDto generateOptions() {
        String token = serviceClient.getAccessTokenFromGigaChat();
        GigaChatRequestDto prompt = GigaChatRequestDto
                .builder()
                .model("GigaChat-2-Max")
                .messages(
                        List.of(
                                GigaChatRequestDto.Message
                                        .builder()
                                        .role("system")
                                        .content("Ты генератор ссылок на веб-ресурсы. Сгенерируй 4 ссылки.")
                                        .build(),
                                GigaChatRequestDto.Message
                                        .builder()
                                        .role("user")
                                        .content("Ты дожен быть сгенерировать 4 ссылки, и одна из них должна " +
                                                "быть фишинговой. Твой ответ должен иметь следующий вид: ссылка:" +
                                                " boolean, ссылка: boolean, ссылка: boolean, ссылка: boolean, " +
                                                "explanation: .... Explanation - объяснение, почему ссылка " +
                                                "является фишинговой. Если сгенерированная тобой ссылка" +
                                                " фишинговая, то вместо boolean - true, если не фишинговая " +
                                                "- false. При этом правильных фишинговых ссылок быть несколько.")
                                        .build()
                        )
                )
                .build();
        String response = serviceClient.getResponseFromGigaChat(prompt, token).getContent();
        FindPhishingUrlGameResponseDto result = extractFromResponse(response);
        FindPhishingUrlGameTask entity = FindPhishingUrlGameTask
                .builder()
                .urls(result.getUrls())
                .explanation(result.getExplanation())
                .build();
        result.setExplanation("");
        return result;
    }

    public FindPhishingUrlGameResponseDto extractFromResponse(String response){
        Map<String, Boolean> urls = new HashMap<>();
        while(response.contains("http")){
            String urlPlusPhishFlag = response.substring(response.indexOf("http"), response.indexOf("\n"));
            String url = urlPlusPhishFlag.substring(0, urlPlusPhishFlag.indexOf(":"));
            String flag = urlPlusPhishFlag.substring(urlPlusPhishFlag.indexOf(":") + 1).strip();
            urls.put(url, Boolean.getBoolean(flag));
            response = response.substring(0, response.indexOf("\n"));
        }
        String explanation = response.substring(response.indexOf(":")).strip();
        return FindPhishingUrlGameResponseDto
                .builder()
                .urls(urls)
                .explanation(explanation)
                .build();
    }

}
