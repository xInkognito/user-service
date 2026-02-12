package ru.cinimex.userimpl.exception;

public class UserAlreadyActivatedException extends RuntimeException {
    public UserAlreadyActivatedException(String message) {
        super(message);
    }
}
