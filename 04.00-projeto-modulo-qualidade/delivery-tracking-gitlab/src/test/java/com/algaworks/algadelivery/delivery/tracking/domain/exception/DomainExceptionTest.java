package com.algaworks.algadelivery.delivery.tracking.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainExceptionTest {

    @Test
    void shouldCreateExceptionWithoutMessage() {
        DomainException exception = new DomainException();

        assertNull(exception.getMessage());
    }

    @Test
    void shouldCreateExceptionWithMessage() {
        String message = "Invalid operation";
        DomainException exception = new DomainException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldCreateExceptionWithMessageAndCause() {
        String message = "Invalid operation";
        RuntimeException cause = new RuntimeException("Root cause");
        
        DomainException exception = new DomainException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void shouldBeRuntimeException() {
        DomainException exception = new DomainException();

        assertInstanceOf(RuntimeException.class, exception);
    }
}
