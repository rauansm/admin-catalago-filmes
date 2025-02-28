package com.codelabs.admin.catalago.infrastructure.web.handler;

import br.com.fluentvalidator.context.ValidationResult;
import com.codelabs.admin.catalago.common.exceptions.NotFoundException;
import com.codelabs.admin.catalago.common.exceptions.PhysicalValidationException;
import com.codelabs.admin.catalago.infrastructure.web.handler.errorcustom.ErrorApiFieldResponse;
import com.codelabs.admin.catalago.infrastructure.web.handler.errorcustom.ErrorApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.codelabs.admin.catalago.domain.enums.ProblemType.INVALID_PARAMETER;

@RestControllerAdvice
@Slf4j
public class RestControllerExceptionHandler {

    @ExceptionHandler
    ResponseEntity<Object> handeValidationException(final PhysicalValidationException ex) {
        log.error("Field validation error ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorApiResponse
                        .builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("Field validation error")
                        .timestamp(Instant.now())
                        .fields(this.transformResult(ex.getValidationResult()))
                        .build()
        );
    }

    @ExceptionHandler
    ResponseEntity<Object> handleException(final Exception ex) {
        log.error("Unexpected error encountered {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorApiResponse
                        .builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("Unexpected error. Contact support.")
                        .timestamp(Instant.now())
                        .build()
        );
    }

    @ExceptionHandler
    ResponseEntity<Object> handleMethodValidation(final HandlerMethodValidationException ex) {
        log.error("Invalid parameter ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorApiResponse
                        .builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("Invalid parameter")
                        .timestamp(Instant.now())
                        .fields(this.transformResult(ex.getAllValidationResults()))
                        .build()
        );
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleHttpMessageNotReadableException(final HttpMessageNotReadableException ex) {
        log.error("Invalid request body. {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorApiResponse
                        .builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("Invalid request body")
                        .timestamp(Instant.now())
                        .build()
        );
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleNotFoundException(final NotFoundException ex) {
        log.error("Resource not found.", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorApiResponse
                        .builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .message(ex.getMessage())
                        .timestamp(Instant.now())
                        .build()
        );
    }

    private Collection<ErrorApiFieldResponse> transformResult(ValidationResult validationResult) {
        return Optional.ofNullable(validationResult.getErrors())
                .orElse(Collections.emptyList())
                .stream()
                .map(error -> ErrorApiFieldResponse.builder()
                        .code(error.getCode())
                        .field(error.getField())
                        .message(error.getMessage())
                        .build())
                .collect(Collectors.toList());
    }

    private Collection<ErrorApiFieldResponse> transformResult(List<ParameterValidationResult> validationResults) {
        return Optional.ofNullable(validationResults)
                .orElse(Collections.emptyList())
                .stream()
                .flatMap(result -> result.getResolvableErrors().stream()
                        .map(violation -> ErrorApiFieldResponse.builder()
                                .code(INVALID_PARAMETER.name())
                                .field(result.getMethodParameter().getParameterName())
                                .message(violation.getDefaultMessage())
                                .build()))
                .collect(Collectors.toList());
    }

}
