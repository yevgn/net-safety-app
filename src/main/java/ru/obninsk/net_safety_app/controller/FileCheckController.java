package ru.obninsk.net_safety_app.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.obninsk.net_safety_app.dto.FileCheckResponseDto;
import ru.obninsk.net_safety_app.service.FileCheckService;

@RestController
@RequestMapping("/filecheck")
@RequiredArgsConstructor
//@Tag(
//        name = "Контроллер, для проверки файлов на содержание вредоносных данных"
//)
public class FileCheckController {
    private final FileCheckService fileCheckService;

//    @Operation(
//            summary = "Проверка файла"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "400", description = "Неправильный формат файла",
//                    content = @Content(mediaType = "application/json")),
//            @ApiResponse(responseCode = "200", description = "ОК")
//    })
    @PostMapping("/check-file")
    public ResponseEntity<FileCheckResponseDto> checkFile(@RequestParam("file") MultipartFile file) throws InterruptedException {
        return ResponseEntity.ok(fileCheckService.checkFile(file));
    }
}
