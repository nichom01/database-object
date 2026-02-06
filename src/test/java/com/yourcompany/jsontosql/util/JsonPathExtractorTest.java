package com.yourcompany.jsontosql.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JsonPathExtractorTest {
    
    private JsonPathExtractor jsonPathExtractor;
    
    private static final String SAMPLE_JSON = """
        {
          "user": {
            "name": "john_doe",
            "email": "john@example.com",
            "age": 30,
            "active": true
          },
          "metadata": {
            "created": "2024-01-01"
          }
        }
        """;
    
    @BeforeEach
    void setUp() {
        jsonPathExtractor = new JsonPathExtractor();
    }
    
    @Test
    void testExtractValue_WithDollarPrefix() {
        Optional<Object> result = jsonPathExtractor.extractValue(SAMPLE_JSON, "$.user.name");
        assertTrue(result.isPresent());
        assertEquals("john_doe", result.get());
    }
    
    @Test
    void testExtractValue_WithoutDollarPrefix() {
        Optional<Object> result = jsonPathExtractor.extractValue(SAMPLE_JSON, "user.name");
        assertTrue(result.isPresent());
        assertEquals("john_doe", result.get());
    }
    
    @Test
    void testExtractValue_NestedPath() {
        Optional<Object> result = jsonPathExtractor.extractValue(SAMPLE_JSON, "user.email");
        assertTrue(result.isPresent());
        assertEquals("john@example.com", result.get());
    }
    
    @Test
    void testExtractValue_Number() {
        Optional<Object> result = jsonPathExtractor.extractValue(SAMPLE_JSON, "user.age");
        assertTrue(result.isPresent());
        assertEquals(30, result.get());
    }
    
    @Test
    void testExtractValue_Boolean() {
        Optional<Object> result = jsonPathExtractor.extractValue(SAMPLE_JSON, "user.active");
        assertTrue(result.isPresent());
        assertEquals(true, result.get());
    }
    
    @Test
    void testExtractValue_NonExistentPath() {
        Optional<Object> result = jsonPathExtractor.extractValue(SAMPLE_JSON, "user.nonexistent");
        assertFalse(result.isPresent());
    }
    
    @Test
    void testExtractValue_NullJsonData() {
        Optional<Object> result = jsonPathExtractor.extractValue(null, "user.name");
        assertFalse(result.isPresent());
    }
    
    @Test
    void testExtractValue_EmptyJsonPath() {
        Optional<Object> result = jsonPathExtractor.extractValue(SAMPLE_JSON, "");
        assertFalse(result.isPresent());
    }
    
    @Test
    void testExtractValue_NullJsonPath() {
        Optional<Object> result = jsonPathExtractor.extractValue(SAMPLE_JSON, null);
        assertFalse(result.isPresent());
    }
    
    @Test
    void testExtractStringValue() {
        Optional<String> result = jsonPathExtractor.extractStringValue(SAMPLE_JSON, "user.name");
        assertTrue(result.isPresent());
        assertEquals("john_doe", result.get());
    }
    
    @Test
    void testExtractStringValue_Number() {
        Optional<String> result = jsonPathExtractor.extractStringValue(SAMPLE_JSON, "user.age");
        assertTrue(result.isPresent());
        assertEquals("30", result.get());
    }
    
    @Test
    void testPathExists_ExistingPath() {
        assertTrue(jsonPathExtractor.pathExists(SAMPLE_JSON, "user.name"));
    }
    
    @Test
    void testPathExists_NonExistentPath() {
        assertFalse(jsonPathExtractor.pathExists(SAMPLE_JSON, "user.nonexistent"));
    }
    
    @Test
    void testExtractValue_RootLevel() {
        String simpleJson = "{\"name\": \"test\"}";
        Optional<Object> result = jsonPathExtractor.extractValue(simpleJson, "name");
        assertTrue(result.isPresent());
        assertEquals("test", result.get());
    }
    
    @Test
    void testExtractValue_ArrayElement() {
        String jsonWithArray = "{\"items\": [\"item1\", \"item2\"]}";
        Optional<Object> result = jsonPathExtractor.extractValue(jsonWithArray, "items[0]");
        assertTrue(result.isPresent());
        assertEquals("item1", result.get());
    }
}
