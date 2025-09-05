package ru.obninsk.net_safety_app.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.obninsk.net_safety_app.entity.Role;
import ru.obninsk.net_safety_app.entity.TokenMode;
import ru.obninsk.net_safety_app.entity.TokenType;
import ru.obninsk.net_safety_app.entity.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final TokenService tokenService;
    private final JavaMailSender mailSender;
    @Value("${spring.application.uri.confirm-registration}")
    private String confirmRegistrationUri;
    @Value("${spring.application.uri.confirm-2fa-secret-reset}")
    private String confirm2faSecretResetUri;
    @Value("${spring.application.sender-email}")
    private String senderEmail;

    // предусмотреть выдачу новго токена
    public void sendConfirmationMessage(User user) throws MailSendException, MailAuthenticationException, MessagingException {
        tokenService.revokeUserTokensByTokenModeIn(user.getEmail(), List.of(TokenMode.EMAIL_CONFIRMATION));
        String token = tokenService.generateUserToken(List.of(Role.USER.name()), user.getEmail(), TokenMode.EMAIL_CONFIRMATION);
        tokenService.saveToken(token, TokenType.BEARER, TokenMode.EMAIL_CONFIRMATION, user);

        String subject = "Подтверждение электронной почты";

        String htmlText = String.format("<p>Добрый день!</p>" +
                "<p>Перейдите по ссылке, чтобы подтвердить почту:</p>" +
                "<p><a href=\"%s?token=%s&user_email=%s\">Подтвердить</a></p>", confirmRegistrationUri, token, user.getEmail());

        sendEmail(subject, htmlText, new String[]{user.getEmail()});
    }

    public void send2faResetUriMessage(User user)  throws MailSendException, MailAuthenticationException, MessagingException {
        tokenService.revokeUserTokensByTokenModeIn(user.getEmail(), List.of(TokenMode.RESET_2FA));
        String token = tokenService.generateUserToken(List.of(Role.USER.name()), user.getEmail(), TokenMode.RESET_2FA);
        tokenService.saveToken(token, TokenType.BEARER, TokenMode.RESET_2FA, user);

        String subject = "Сброс 2FA секретного ключа";

        String htmlText = String.format("<p>Добрый день!</p>" +
                "<p>Перейдите по ссылке, чтобы сбросить 2FA секретный ключ:</p>" +
                "<p><a href=\"%s?token=%s&user_email=%s\">Подтвердить</a></p>", confirm2faSecretResetUri, token, user.getEmail());

        sendEmail(subject, htmlText, new String[]{user.getEmail()});
    }

    private void sendEmail(String subject, String text, String[] sendTo)
            throws MailSendException, MailAuthenticationException,MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(sendTo);
        helper.setSubject(subject);
        helper.setText(text, true);
        helper.setFrom(senderEmail);
        mailSender.send(message);
    }

    public void send2faSecretResetNotification(User user)
            throws MailSendException, MailAuthenticationException,MessagingException {
        String subject = "Уведомление о сбросе 2FA секретного ключа.";

        String htmlText = "<p>Добрый день! Ваш 2FA секретный ключ был сброшен</p>" +
                "<p>Для того, чтобы получить новый ключ, необходимо заново подключить двухфакторную аутентификацию</p>" +
                "<p>Если это были не вы, свяжитесь со службой поддержки</p>";

        sendEmail(subject, htmlText, new String[]{user.getEmail()});
    }
}
