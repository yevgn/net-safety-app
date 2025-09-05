package ru.obninsk.net_safety_app.exception;

public class TokenValidationFailureException extends RuntimeException{
    public TokenValidationFailureException(String msg){
        super(msg);
    }
}
