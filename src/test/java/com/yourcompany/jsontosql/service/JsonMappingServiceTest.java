package com.yourcompany.jsontosql.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourcompany.jsontosql.model.ColumnDefinition;
import com.yourcompany.jsontosql.model.TableDefinition;
import com.yourcompany.jsontosql.util.JsonPathExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JsonMappingServiceTest {
    
    @Mock
    private JsonPathExtractor jsonPathExtractor;
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    private JsonMappingService jsonMappingService;
    
    private TableDefinition tableDefinition;
    private String jsonData;
    
    @BeforeEach
    void setUp() {
        jsonMappingService = new JsonMappingService(jsonPathExtractor, objectMapper);
        
        tableDefinition = TableDefinition.builder()
                .tableName("users")
                .columns(Arrays.asList(
                        ColumnDefinition.builder()
                                .name("username")
                                .type("VARCHAR(255)")
                                .nullable(false)
                                .jsonPath("user.name")
                                .build(),
                        ColumnDefinition.builder()
                                .name("email")
                                .type("VARCHAR(255)")
                                .nullable(true)
                                .jsonPath("user.email")
                                .build()
                ))
                .build();
        
        jsonData = "{\"user\":{\"name\":\"john_doe\",\"email\":\"john@example.com\"}}";
    }
    
    @Test
    void testValidateJsonAgainstSchema_Valid() {
        when(jsonPathExtractor.extractValue(anyString(), org.mockito.ArgumentMatchers.eq("user.name")))
                .thenReturn(Optional.of("john_doe"));
        when(jsonPathExtractor.extractValue(anyString(), org.mockito.ArgumentMatchers.eq("user.email")))
                .thenReturn(Optional.of("john@example.com"));
        
        Map<String, Object> result = jsonMappingService.validateJsonAgainstSchema(tableDefinition, jsonData);
        
        assertNotNull(result);
        assertTrue((Boolean) result.get("valid"));
        assertTrue(((java.util.List<?>) result.get("errors")).isEmpty());
    }
    
    @Test
    void testValidateJsonAgainstSchema_MissingRequiredField() {
        when(jsonPathExtractor.extractValue(anyString(), org.mockito.ArgumentMatchers.eq("user.name")))
                .thenReturn(Optional.empty());
        when(jsonPathExtractor.extractValue(anyString(), org.mockito.ArgumentMatchers.eq("user.email")))
                .thenReturn(Optional.of("john@example.com"));
        
        Map<String, Object> result = jsonMappingService.validateJsonAgainstSchema(tableDefinition, jsonData);
        
        assertNotNull(result);
        assertFalse((Boolean) result.get("valid"));
        assertFalse(((java.util.List<?>) result.get("errors")).isEmpty());
    }
    
    @Test
    void testValidateJsonAgainstSchema_OptionalFieldMissing() {
        when(jsonPathExtractor.extractValue(anyString(), org.mockito.ArgumentMatchers.eq("user.name")))
                .thenReturn(Optional.of("john_doe"));
        when(jsonPathExtractor.extractValue(anyString(), org.mockito.ArgumentMatchers.eq("user.email")))
                .thenReturn(Optional.empty());
        
        Map<String, Object> result = jsonMappingService.validateJsonAgainstSchema(tableDefinition, jsonData);
        
        assertNotNull(result);
        assertTrue((Boolean) result.get("valid")); // Email is nullable, so should be valid
    }
    
    @Test
    void testMapJsonToColumns() {
        when(jsonPathExtractor.extractValue(anyString(), org.mockito.ArgumentMatchers.eq("user.name")))
                .thenReturn(Optional.of("john_doe"));
        when(jsonPathExtractor.extractValue(anyString(), org.mockito.ArgumentMatchers.eq("user.email")))
                .thenReturn(Optional.of("john@example.com"));
        
        Map<String, Object> result = jsonMappingService.mapJsonToColumns(tableDefinition, jsonData);
        
        assertNotNull(result);
        assertEquals("john_doe", result.get("username"));
        assertEquals("john@example.com", result.get("email"));
    }
    
    @Test
    void testMapJsonToColumns_WithDefaultValue() {
        ColumnDefinition columnWithDefault = ColumnDefinition.builder()
                .name("status")
                .type("VARCHAR(50)")
                .nullable(true)
                .defaultValue("active")
                .build();
        tableDefinition.getColumns().add(columnWithDefault);
        
        when(jsonPathExtractor.extractValue(anyString(), org.mockito.ArgumentMatchers.eq("user.name")))
                .thenReturn(Optional.of("john_doe"));
        when(jsonPathExtractor.extractValue(anyString(), org.mockito.ArgumentMatchers.eq("user.email")))
                .thenReturn(Optional.of("john@example.com"));
        when(jsonPathExtractor.extractValue(anyString(), org.mockito.ArgumentMatchers.eq("status")))
                .thenReturn(Optional.empty());
        
        Map<String, Object> result = jsonMappingService.mapJsonToColumns(tableDefinition, jsonData);
        
        assertNotNull(result);
        assertEquals("active", result.get("status"));
    }
}
