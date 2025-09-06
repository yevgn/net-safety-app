package ru.obninsk.net_safety_app.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
public class ApiErrorList {
    private HttpStatus status;
    private String message;
    private List<String> errors;
}
