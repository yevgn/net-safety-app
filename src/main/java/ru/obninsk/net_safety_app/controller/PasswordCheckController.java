package ru.obninsk.net_safety_app.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.obninsk.net_safety_app.dto.PasswordCheckRequestDto;
import ru.obninsk.net_safety_app.dto.PasswordCheckResponseDto;
import ru.obninsk.net_safety_app.service.PasswordCheckService;

@RestController
@RequestMapping("/passwordcheck")
@RequiredArgsConstructor
//@Tag(
//        name = "Контроллер для проверки паролей"
//)
public class PasswordCheckController {
    private final PasswordCheckService passwordCheckService;

//    @Operation(
//            summary = "Проверка надежности пароля"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "400", description = "Пароль отсутствует"),
//            @ApiResponse(responseCode = "200", description = "ОК")
//    })
    @PostMapping("/check-password-strength")
    public ResponseEntity<PasswordCheckResponseDto> checkPasswordStrength(
            @Valid @RequestBody PasswordCheckRequestDto request){
        return ResponseEntity.ok(passwordCheckService.checkPasswordStrength(request));
    }

}
