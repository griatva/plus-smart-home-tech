package ru.yandex.practicum.interaction.exception;


public class ProductReturnException extends RuntimeException {
    public ProductReturnException(String message) {
        super(message);
    }
}