package ru.obninsk.net_safety_app.exception;

public class EmailNotConfirmedException extends RuntimeException {
  public EmailNotConfirmedException(String message) {
    super(message);
  }
}
