package ru.yandex.practicum.warehouse.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.interaction.exception.ErrorResponseDto;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SpecifiedProductAlreadyInWarehouseException.class) //400
    public ResponseEntity<ErrorResponseDto> handleProductAlreadyInWarehouse(SpecifiedProductAlreadyInWarehouseException ex) {
        ErrorResponseDto response = ErrorResponseDto.buildFrom(ex,
                "Товар с таким описанием уже зарегистрирован на складе", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductInShoppingCartLowQuantityInWarehouse.class) //400
    public ResponseEntity<ErrorResponseDto> handleLowQuantity(ProductInShoppingCartLowQuantityInWarehouse ex) {
        ErrorResponseDto response = ErrorResponseDto.buildFrom(ex,
                "Товар из корзины не находится в требуемом количестве на складе",
                HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class) //400
    public ResponseEntity<ErrorResponseDto> handleLowQuantity(NoSpecifiedProductInWarehouseException ex) {
        ErrorResponseDto response = ErrorResponseDto.buildFrom(ex, "Нет информации о товаре на складе",
                HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    // NotFoundException
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handlerNotFoundException(final NotFoundException e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        log.error("Ошибка: 404 NOT_FOUND - {}", stackTrace);
        return new ApiError("Объект не найден или недоступен", e.getMessage(),
                HttpStatus.NOT_FOUND.name(), LocalDateTime.now());
    }


    // Общий обработчик (fallback)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handlerException(final Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        log.error("Ошибка: 500 INTERNAL_SERVER_ERROR - {}", stackTrace);
        return new ApiError("Неизвестная ошибка", e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.name(), LocalDateTime.now());
    }


    // QuantityStateUpdateException
    @ExceptionHandler(QuantityStateUpdateException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ApiError handleQuantityStateUpdateException(final QuantityStateUpdateException e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        log.error("Ошибка: 502 BAD_GATEWAY - {}", stackTrace);
        return new ApiError(
                "Ошибка при обновлении статуса количества товара",
                e.getMessage(),
                HttpStatus.BAD_GATEWAY.name(),
                LocalDateTime.now()
        );
    }

    @Data
    @Builder
    public static class ApiError {
        private String message;
        private String reason;
        private String status;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime timestamp;
    }

}