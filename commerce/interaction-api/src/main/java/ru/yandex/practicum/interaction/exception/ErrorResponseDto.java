package ru.yandex.practicum.interaction.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {
    private CauseDto cause;
    private List<StackTraceItem> stackTrace;
    private String httpStatus;
    private String userMessage;
    private String message;
    private List<SuppressedItem> suppressed;
    private String localizedMessage;

    public static ErrorResponseDto buildFrom(Throwable ex, String userMessage, HttpStatus status) {
        List<StackTraceItem> stackTrace = Arrays.stream(ex.getStackTrace())
                .map(StackTraceItem::fromStackTraceElement)
                .collect(Collectors.toList());

        CauseDto cause = null;
        if (ex.getCause() != null) {
            cause = CauseDto.builder()
                    .stackTrace(Arrays.stream(ex.getCause().getStackTrace())
                            .map(StackTraceItem::fromStackTraceElement)
                            .collect(Collectors.toList()))
                    .message(ex.getCause().getMessage())
                    .localizedMessage(ex.getCause().getLocalizedMessage())
                    .build();
        }

        List<SuppressedItem> suppressed = Arrays.stream(ex.getSuppressed())
                .map(s -> SuppressedItem.builder()
                        .stackTrace(Arrays.stream(s.getStackTrace())
                                .map(StackTraceItem::fromStackTraceElement)
                                .collect(Collectors.toList()))
                        .message(s.getMessage())
                        .localizedMessage(s.getLocalizedMessage())
                        .build())
                .collect(Collectors.toList());

        ErrorResponseDto dto = new ErrorResponseDto();
        dto.setCause(cause);
        dto.setStackTrace(stackTrace);
        dto.setHttpStatus(status.value() + " " + status.name());
        dto.setUserMessage(userMessage);
        dto.setMessage(ex.getMessage());
        dto.setSuppressed(suppressed);
        dto.setLocalizedMessage(ex.getLocalizedMessage());
        return dto;
    }

    @Data
    @Builder
    public static class StackTraceItem {
        private String classLoaderName;
        private String moduleName;
        private String moduleVersion;
        private String methodName;
        private String fileName;
        private Integer lineNumber;
        private String className;
        private Boolean nativeMethod;

        public static StackTraceItem fromStackTraceElement(StackTraceElement e) {
            return StackTraceItem.builder()
                    .classLoaderName(null)
                    .moduleName(null)
                    .moduleVersion(null)
                    .methodName(e.getMethodName())
                    .fileName(e.getFileName())
                    .lineNumber(e.getLineNumber())
                    .className(e.getClassName())
                    .nativeMethod(e.isNativeMethod())
                    .build();
        }
    }

    @Data
    @Builder
    public static class SuppressedItem {
        private List<StackTraceItem> stackTrace;
        private String message;
        private String localizedMessage;
    }

    @Data
    @Builder
    public static class CauseDto {
        private List<StackTraceItem> stackTrace;
        private String message;
        private String localizedMessage;
    }
}