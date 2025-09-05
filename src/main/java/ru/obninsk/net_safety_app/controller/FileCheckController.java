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
public class FileCheckController {
    private final FileCheckService fileCheckService;

    @PostMapping("/check-file")
    public ResponseEntity<FileCheckResponseDto> checkFile(@RequestParam("file") MultipartFile file){
        return ResponseEntity.ok(fileCheckService.checkFile(file));
    }


}
