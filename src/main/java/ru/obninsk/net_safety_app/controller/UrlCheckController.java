package ru.obninsk.net_safety_app.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.obninsk.net_safety_app.dto.PageResponseDto;
import ru.obninsk.net_safety_app.dto.UrlCheckRequestDto;
import ru.obninsk.net_safety_app.dto.UrlCheckResponseDto;
import ru.obninsk.net_safety_app.dto.UrlCheckShortResponseDto;
import ru.obninsk.net_safety_app.entity.ResultCategory;
import ru.obninsk.net_safety_app.entity.User;
import ru.obninsk.net_safety_app.service.UrlCheckResultService;
import ru.obninsk.net_safety_app.service.UrlCheckService;

@RestController
@RequestMapping("/urlcheck")
@RequiredArgsConstructor
@Validated
//@Tag(
//        name = "Контроллер для проверки ссылок на веб-ресурсы"
//)
public class UrlCheckController {
    private final UrlCheckResultService urlCheckResultService;
    private final UrlCheckService urlCheckService;


//    @Operation(
//            summary = "Проверка ссылки на веб-ресурс",
//            description = "Ссылка проверяется на фишинг, сайт, на который ведет ссылка - на содержание вредоносных ресурсов." +
//                    "Вместо с вердиктом пользователь получает скриншот сайта, на который ведет ссылка"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "400", description = "Ссылка отсутствует"),
//            @ApiResponse(responseCode = "200", description = "ОК")
//    })
    @PostMapping("/check-url")
    public ResponseEntity<UrlCheckResponseDto> checkUrl(
            @Valid @RequestBody UrlCheckRequestDto request, @AuthenticationPrincipal User user) throws InterruptedException {
        return ResponseEntity.ok(urlCheckService.checkUrl(request, user));
    }

//    @Operation(
//            summary = "Получение сохраненных ссылок"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
//            @ApiResponse(responseCode = "200", description = "ОК")
//    })
    @GetMapping("/checked-url-list")
    public ResponseEntity<PageResponseDto<UrlCheckShortResponseDto>> findUrlCheckResults(
            @Min(value = 0, message = "Минимальное значение offset - 0")
            @Max(value = Integer.MAX_VALUE, message = "Максимальное значение offset - " + Integer.MAX_VALUE)
            @RequestParam(defaultValue = "0")
            Integer offset,

            @Min(value = 0, message = "Минимальное значение limit - 0")
            @Max(value = 100, message = "Максимальное значение limit - 100")
            @RequestParam(defaultValue = "10")
            Integer limit,

            @RequestParam(required = false) ResultCategory category
            ){
        return ResponseEntity.ok(urlCheckResultService.findAllByCategoryOrderByMadeAt(category, offset, limit));
    }
}
