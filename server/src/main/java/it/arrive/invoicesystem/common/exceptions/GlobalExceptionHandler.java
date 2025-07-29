package it.arrive.invoicesystem.common.exceptions;

import it.arrive.invoicesystem.invoice.exceptions.InvoiceAlreadyPaidException;
import it.arrive.invoicesystem.invoice.exceptions.OutOfStockException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler ( IllegalArgumentException.class )
    public ResponseEntity<ErrorResponse> handleIllegalArgument( IllegalArgumentException ex ) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Invalid argument",
                ex.getMessage()
        );
        return ResponseEntity.badRequest().body( error );
    }

    @ExceptionHandler ( OutOfStockException.class )
    public ResponseEntity<ErrorResponse> handleOutOfStock( OutOfStockException ex ) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Out of stock",
                ex.getMessage()
        );
        return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( error );
    }

    @ExceptionHandler ( InvoiceAlreadyPaidException.class )
    public ResponseEntity<ErrorResponse> handleInvoiceAlreadyPaid( InvoiceAlreadyPaidException ex ) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Out of stock",
                ex.getMessage()
        );
        return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( error );
    }

    @ExceptionHandler ( ResourceNotFoundException.class )
    public ResponseEntity<ErrorResponse> handleNotFound( ResourceNotFoundException ex ) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Resource not found",
                ex.getMessage()
        );
        return ResponseEntity
                .status( HttpStatus.NOT_FOUND )
                .body( error );
    }

    @ExceptionHandler ( MethodArgumentNotValidException.class )
    public ResponseEntity<ErrorResponse> handleValidation( MethodArgumentNotValidException ex ) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map( error -> error.getField() + ": " + error.getDefaultMessage() )
                .findFirst()
                .orElse( ex.getMessage() );

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                message
        );
        return ResponseEntity.badRequest().body( error );
    }

    @ExceptionHandler ( ConstraintViolationException.class )
    public ResponseEntity<ErrorResponse> handleConstraintViolation( ConstraintViolationException ex ) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Constraint violation",
                ex.getMessage()
        );
        return ResponseEntity.badRequest().body( error );
    }

    @ExceptionHandler ( HttpRequestMethodNotSupportedException.class )
    public ResponseEntity<ErrorResponse> handleMethodNotSupported( HttpRequestMethodNotSupportedException ex ) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "Method not allowed",
                ex.getMessage()
        );
        return ResponseEntity.status( HttpStatus.METHOD_NOT_ALLOWED ).body( error );
    }

    @ExceptionHandler ( Exception.class )
    public ResponseEntity<ErrorResponse> handleAllUnhandledExceptions( Exception ex ) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error",
                ex.getMessage()
        );
        return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR ).body( error );
    }
}
