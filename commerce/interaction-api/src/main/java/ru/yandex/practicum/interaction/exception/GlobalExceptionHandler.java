package ru.yandex.practicum.interaction.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.OffsetDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---------- 400 BAD_REQUEST ----------
    @ExceptionHandler({
            NotEnoughInfoInOrderToCalculateException.class,
            NoProductsInShoppingCartException.class,
            SpecifiedProductAlreadyInWarehouseException.class,
            ProductInShoppingCartLowQuantityInWarehouse.class,
            NoSpecifiedProductInWarehouseException.class,
            ProductInShoppingCartNotInWarehouse.class,
            IllegalArgumentException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(final Exception e) {
        logException(e, HttpStatus.BAD_REQUEST);
        return buildApiError(e, "Bad request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final MethodArgumentNotValidException e) {
        logException(e, HttpStatus.BAD_REQUEST);
        return buildApiError(e, "Request validation failed", HttpStatus.BAD_REQUEST);
    }

    // ---------- 401 UNAUTHORIZED ----------
    @ExceptionHandler(NotAuthorizedUserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiError handleUnauthorized(final NotAuthorizedUserException e) {
        logException(e, HttpStatus.UNAUTHORIZED);
        return buildApiError(e, "User is not authorized", HttpStatus.UNAUTHORIZED);
    }

    // ---------- 404 NOT_FOUND ----------
    @ExceptionHandler({
            NoDeliveryFoundException.class,
            NoOrderFoundException.class,
            ProductNotFoundException.class,
            NotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final Exception e) {
        logException(e, HttpStatus.NOT_FOUND);
        return buildApiError(e, "Resource not found", HttpStatus.NOT_FOUND);
    }

    // ---------- 409 CONFLICT ----------
    @ExceptionHandler({
            ProductReturnException.class,
            ConflictException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(final Exception e) {
        logException(e, HttpStatus.CONFLICT);
        return buildApiError(e, "Data conflict", HttpStatus.CONFLICT);
    }

    // ---------- 502 BAD_GATEWAY ----------
    @ExceptionHandler(QuantityStateUpdateException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ApiError handleBadGateway(final QuantityStateUpdateException e) {
        logException(e, HttpStatus.BAD_GATEWAY);
        return buildApiError(e, "Error while updating product quantity", HttpStatus.BAD_GATEWAY);
    }

    // ------- 500 INTERNAL_SERVER_ERROR -----
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleUnknownException(final Exception e) {
        logException(e, HttpStatus.INTERNAL_SERVER_ERROR);
        return buildApiError(e, "Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void logException(Exception e, HttpStatus status) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        log.error("Error: {} - {}", status, sw);
    }

    private ApiError buildApiError(Exception e, String userMessage, HttpStatus status) {
        return ApiError.builder()
                .message(userMessage)
                .reason(e.getMessage())
                .status(status.name())
                .exception(e.getClass().getSimpleName())
                .timestamp(OffsetDateTime.now())
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiError {
        private String message;
        private String reason;
        private String status;
        private String exception;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private OffsetDateTime timestamp;
    }
}