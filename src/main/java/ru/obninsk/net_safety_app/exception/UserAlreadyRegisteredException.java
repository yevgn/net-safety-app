package ru.obninsk.net_safety_app.exception;

public class UserAlreadyRegisteredException extends RuntimeException {
  public UserAlreadyRegisteredException(String message) {
    super(message);
  }
}
