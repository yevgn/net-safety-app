package ru.obninsk.net_safety_app.exception;

public class ConfirmationTokenRenewException extends RuntimeException {
    public ConfirmationTokenRenewException(String message) {
        super(message);
    }
}
