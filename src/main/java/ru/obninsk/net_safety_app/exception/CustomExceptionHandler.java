package ru.obninsk.net_safety_app.exception;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.validation.ConstraintDeclarationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {
    @ExceptionHandler({  MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class,
            NoResourceFoundException.class,
            UsernameNotFoundException.class,  EntityNotFoundException.class} )
    public ResponseEntity<ApiError> handle400Exceptions(Exception ex) throws Exception {
        log.info(ex.getMessage());
        ApiError error = ApiError
                .builder()
                .status(HttpStatus.BAD_REQUEST)
                .error(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException ex){
        log.info("Аутентификация не пройдена\n" + ex.getMessage());
        ApiError error = ApiError
                .builder()
                .status(HttpStatus.UNAUTHORIZED)
                .error("Аутентификация не пройдена")
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler( TokenValidationFailureException.class )
    public ResponseEntity<ApiError> handleTokenValidationFailureEx(TokenValidationFailureException ex){
        log.info(String.format("Токен не прошел валидацию\n" + ex.getMessage()));
        ApiError error = ApiError
                .builder()
                .status(HttpStatus.UNAUTHORIZED)
                .error(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(TfaAlreadyEnabledException.class)
    public ResponseEntity<ApiError> handleTfaAlreadyEnabledException(TfaAlreadyEnabledException ex){
        log.info("Ошибка при подключении 2FA. 2FA уже активна:\n" + ex.getMessage());
        ApiError error = ApiError
                .builder()
                .status(HttpStatus.BAD_REQUEST)
                .error(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(UserNotActivatedException.class)
    public ResponseEntity<ApiError> handleUserNotActivatedException(UserNotActivatedException ex){
        log.info("Попытка доступа неактивированного пользователя:\n" + ex.getMessage());
        ApiError error = ApiError
                .builder()
                .status(HttpStatus.FORBIDDEN)
                .error(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationEx(ConstraintViolationException ex){
        ApiError apiError = ApiError
                .builder()
                .status(HttpStatus.CONFLICT)
                .error(List.of(ex.getSQLException().getMessage()).toString())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<ApiErrorList> handleJakValidConstraintViolationEx(jakarta.validation.ConstraintViolationException ex){
        List<String> errors = new ArrayList<>();

        ex.getConstraintViolations().forEach(v -> {
            errors.add(v.getMessage());
        });

        ApiErrorList apiError = ApiErrorList
                .builder()
                .status(HttpStatus.BAD_REQUEST)
                .errors(errors)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public ResponseEntity<ApiError> handleUserAlreadyRegisteredException(UserAlreadyRegisteredException ex){
        log.info("Пользователь с такими данными уже зарегистрирован\n" + ex.getMessage());
        ApiError error = ApiError
                .builder()
                .status(HttpStatus.BAD_REQUEST)
                .error(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorList> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.add(error.getDefaultMessage())
        );

        ApiErrorList error = ApiErrorList
                .builder()
                .status(HttpStatus.BAD_REQUEST)
                .errors(errors)
                .build();

        return ResponseEntity.badRequest().body(error);
    }

//    @ExceptionHandler(HandlerMethodValidationException.class)
//    public ResponseEntity<ApiErrorList> handleHandlerMethodValidationEx(HandlerMethodValidationException ex) {
//        log.info( ex.getMessage());
//        List<String> errors = new ArrayList<>();
//
//        ex.getParameterValidationResults().forEach(validationRes ->
//                validationRes.getResolvableErrors().forEach( error ->
//                        errors.add(error.getDefaultMessage())
//                ));
//
//        ApiErrorList error = ApiErrorList
//                .builder()
//                .status(HttpStatus.BAD_REQUEST)
//                .errors(errors)
//                .build();
//
//        return ResponseEntity.badRequest().body(error);
//    }

    @ExceptionHandler({ InternalServerErrorException.class, Qr2faGenerationException.class,
            ServletException.class, IOException.class})
    public ResponseEntity<ApiError> handle500Exceptions(Exception ex){
        log.error("Error: ", ex);
        ApiError error = ApiError
                .builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Ошибка на сервере")
                .build();
        return ResponseEntity.internalServerError().body(error);
    }

    @ExceptionHandler(MailAuthenticationException.class)
    public ResponseEntity<ApiError> handleMailAuthenticationEx(MailAuthenticationException ex){
        log.error("Ошибка при отправке письма по SMTP: неудачная аутентификация\n" + ex.getMessage());
        ApiError error = ApiError
                .builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Ошибка на сервере")
                .build();
        return ResponseEntity.internalServerError().body(error);
    }

    @ExceptionHandler(MailSendException.class)
    public ResponseEntity<ApiError> handleMailSendException(MailSendException ex){
        log.info("Ошибка при отправке письма по SMTP\n" + ex.getMessage());
        ApiError error = ApiError
                .builder()
                .status(HttpStatus.BAD_GATEWAY)
                .message("Ошибка при отправке электронного письма")
                .build();
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }

    @ExceptionHandler({ BadCredentialsException.class})
    public ResponseEntity<ApiError> handle401Exception(Exception ex){
        log.info(ex.getMessage());
        ApiError error = ApiError
                .builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ApiError> handleMessagingException(MessagingException ex){
        log.error("Ошибка при создании mimeMessage\n" + ex.getMessage());
        ApiError error = ApiError
                .builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Ошибка на сервере")
                .build();
        return ResponseEntity.internalServerError().body(error);
    }

    @ExceptionHandler(InvalidArgumentException.class)
    public ResponseEntity<ApiError> handleInvalidUrlException(InvalidArgumentException ex){
        log.info("Неверный аргумент\n" + ex.getMessage());
        ApiError error = ApiError
                .builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("Ошибка. Проверьте введенные данные")
                .build();
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(JsonSerializationException.class)
    public ResponseEntity<ApiError> handleJsonSerializationException(JsonSerializationException ex){
        log.error("Ошибка при сериализации JSON\n" + ex.getMessage());
        ApiError error = ApiError
                .builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Ошибка на сервере")
                .build();
        return ResponseEntity.internalServerError().body(error);
    }

    @ExceptionHandler(ConfirmationTokenRenewException.class)
    public ResponseEntity<ApiError> handleConfirmationTokenRenewException(ConfirmationTokenRenewException ex){
        log.info("Выдача нового EMAIL_CONFIRMATION_TOKEN:\n"+ ex.getMessage());
        ApiError info = ApiError
                .builder()
                .status(HttpStatus.OK)
                .message("Срок действия ссылки истек. Вы получите новую ссылку на ваш email в ближайшее время")
                .build();
        return ResponseEntity.ok().body(info);
    }

    @ExceptionHandler(Reset2faSecretTokenRenewException.class)
    public ResponseEntity<ApiError> handleReset2faSecretTokenRenewException(Reset2faSecretTokenRenewException ex){
        log.info("Выдача нового RESET_2FA_TOKEN:\n"+ ex.getMessage());
        ApiError info = ApiError
                .builder()
                .status(HttpStatus.OK)
                .message("Срок действия ссылки истек. Вы получите новую ссылку на ваш email в ближайшее время")
                .build();
        return ResponseEntity.ok().body(info);
    }

    @ExceptionHandler(EmailNotConfirmedException.class)
    public ResponseEntity<ApiError> handleEmailNotConfirmedException(EmailNotConfirmedException ex){
        ApiError error = ApiError
                .builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(FileTooLargeException.class)
    public ResponseEntity<ApiError> handleFileTooLargeException(FileTooLargeException ex){
        ApiError error = ApiError
                .builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(InvalidPageParamsExeption.class)
    public ResponseEntity<ApiError> handleInvalidPageParamsException(InvalidPageParamsExeption ex){
        ApiError error = ApiError
                .builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.badRequest().body(error);
    }


    @ExceptionHandler(UserAlreadyActivatedException.class)
    public ResponseEntity<ApiError> handleUserAlreadyActivatedException(UserAlreadyActivatedException ex){
        ApiError error = ApiError
                .builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.badRequest().body(error);
    }
}
