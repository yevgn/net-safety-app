package ru.obninsk.net_safety_app.service;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;

import ru.obninsk.net_safety_app.dto.DtoFactory;
import ru.obninsk.net_safety_app.dto.UserResponseDto;
import ru.obninsk.net_safety_app.entity.TokenMode;
import ru.obninsk.net_safety_app.entity.User;
import ru.obninsk.net_safety_app.exception.ConfirmationTokenRenewException;
import ru.obninsk.net_safety_app.exception.DataMismatchException;
import ru.obninsk.net_safety_app.exception.UserAlreadyActivatedException;
import ru.obninsk.net_safety_app.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final EmailService emailService;

    User save(User user) {
       return userRepository.saveAndFlush(user);
    }

    Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    List<User> findAll(){ return userRepository.findAll(); }

    public void confirmEmail(String email, String token)
            throws MailSendException, MailAuthenticationException, MessagingException {

        User user = findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Пользователь с %s email не найден", email)
                ));

        if(!tokenService.isTokenValid(token, TokenMode.EMAIL_CONFIRMATION)){
            emailService.sendConfirmationMessage(user);
            throw new ConfirmationTokenRenewException(
                    String.format("EMAIL_CONFIRMATION_TOKEN польвателя %s истек. Произошла выдача нового токена", email)
            );
        }

        User tokenUser = tokenService.findUserByToken(token)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Пользователь по токену %s не найден", token )
                ));

        if(!tokenUser.equals(user)){
            throw new DataMismatchException("Указанный email не совпадает с email владельца токена");
        }

        user.setActivated(true);
        save(user);
    }

    public void sendConfirmationMessage(User user)
            throws MailSendException, MailAuthenticationException, MessagingException{
        if(user.isActivated()){
            throw new UserAlreadyActivatedException(
                    String.format("Пользователь %s уже активировал аккаунт", user.getEmail())
            );
        }

        emailService.sendConfirmationMessage(user);
    }

    public UserResponseDto findUserDtoByEmail(String principalEmail) {
        User user = findByEmail(principalEmail)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Пользователь с %s email не найден", principalEmail)
                ));
        return DtoFactory.makeUserResponseDto(user);
    }

    public List<UserResponseDto> findAllUsersDto(){
        return findAll().stream()
                .map(DtoFactory::makeUserResponseDto)
                .toList();
    }
}
