package ru.yandex.practicum.warehouse.exception;

public class QuantityStateUpdateException extends RuntimeException {
    public QuantityStateUpdateException(String message) {
        super(message);
    }
}
