package ru.obninsk.net_safety_app.exception;

public class TfaAlreadyEnabledException extends RuntimeException {
  public TfaAlreadyEnabledException(String message) {
    super(message);
  }
}
