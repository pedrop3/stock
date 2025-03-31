package com.learn.stock.exeption;

import com.learn.stock.expection.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setup() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleIllegalArgument_shouldReturnBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("Campo inválido");

        var response = handler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Campo inválido", response.getBody().message());
        assertEquals("IllegalArgumentException", response.getBody().exceptionName());
        assertEquals(List.of("Campo inválido"), response.getBody().errors());
    }

    @Test
    void handleGenericException_shouldReturnInternalServerError() {
        Exception ex = new RuntimeException("Falha inesperada");

        var response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("internal server error.", response.getBody().message());
        assertEquals("Exception", response.getBody().exceptionName());
        assertEquals(List.of("Falha inesperada"), response.getBody().errors());
    }
}
