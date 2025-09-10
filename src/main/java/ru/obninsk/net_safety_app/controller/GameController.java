package ru.obninsk.net_safety_app.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.obninsk.net_safety_app.dto.FindPhishingUrlGameResponseDto;
import ru.obninsk.net_safety_app.service.GameService;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
@Tag(
        name = "Контроллер для мини-игр"
)
public class GameController {
    private final GameService gameService;

    @Operation(
            summary = "Получение 4 ссылок на web-ресурсы для мини-игры \"Найди фишинговую ссылку\" "
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ОК")
    })
    @GetMapping("/generate-options")
    public ResponseEntity<FindPhishingUrlGameResponseDto> generateOptions(){
        return ResponseEntity.ok(gameService.generateOptions());
    }
}
