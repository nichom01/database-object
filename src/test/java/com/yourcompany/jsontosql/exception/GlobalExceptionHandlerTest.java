package com.yourcompany.jsontosql.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {
    
    private GlobalExceptionHandler exceptionHandler;
    
    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }
    
    @Test
    void testHandleTableDefinitionNotFound() {
        TableDefinitionNotFoundException ex = new TableDefinitionNotFoundException("users");
        
        ResponseEntity<Map<String, Object>> response = 
                exceptionHandler.handleTableDefinitionNotFound(ex);
        
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Table Definition Not Found", response.getBody().get("error"));
        assertTrue(response.getBody().get("message").toString().contains("users"));
    }
    
    @Test
    void testHandleJsonMappingException() {
        JsonMappingException ex = new JsonMappingException("Invalid JSON format");
        
        ResponseEntity<Map<String, Object>> response = 
                exceptionHandler.handleJsonMappingException(ex);
        
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("JSON Mapping Error", response.getBody().get("error"));
    }
    
    @Test
    void testHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");
        
        ResponseEntity<Map<String, Object>> response = 
                exceptionHandler.handleIllegalArgumentException(ex);
        
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid Argument", response.getBody().get("error"));
    }
    
    @Test
    void testHandleGenericException() {
        Exception ex = new Exception("Unexpected error");
        
        ResponseEntity<Map<String, Object>> response = 
                exceptionHandler.handleGenericException(ex);
        
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal Server Error", response.getBody().get("error"));
    }
}
