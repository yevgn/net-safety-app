package ru.obninsk.net_safety_app.exception;

public class Qr2faGenerationException extends RuntimeException {
    public Qr2faGenerationException(String message) {
        super(message);
    }
}
