package com.yourcompany.jsontosql.generator;

import com.yourcompany.jsontosql.model.ColumnDefinition;
import com.yourcompany.jsontosql.model.TableDefinition;
import com.yourcompany.jsontosql.util.JsonPathExtractor;
import com.yourcompany.jsontosql.util.SqlEscapeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InsertStatementGeneratorTest {
    
    @Mock
    private JsonPathExtractor jsonPathExtractor;
    
    @Mock
    private SqlEscapeUtil sqlEscapeUtil;
    
    @InjectMocks
    private InsertStatementGenerator insertStatementGenerator;
    
    private TableDefinition tableDefinition;
    
    @BeforeEach
    void setUp() {
        tableDefinition = TableDefinition.builder()
                .tableName("users")
                .columns(Arrays.asList(
                        ColumnDefinition.builder()
                                .name("id")
                                .type("BIGINT")
                                .nullable(false)
                                .primaryKey(true)
                                .autoIncrement(true)
                                .build(),
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
        
        // Setup default mocks
        when(sqlEscapeUtil.escapeIdentifier(anyString())).thenAnswer(invocation -> {
            String arg = invocation.getArgument(0);
            return "\"" + arg + "\"";
        });
    }
    
    @Test
    void testGenerateInsert_Basic() {
        String jsonData = "{\"user\":{\"name\":\"john_doe\",\"email\":\"john@example.com\"}}";
        
        when(jsonPathExtractor.extractValue(anyString(), org.mockito.ArgumentMatchers.eq("user.name")))
                .thenReturn(java.util.Optional.of("john_doe"));
        when(jsonPathExtractor.extractValue(anyString(), org.mockito.ArgumentMatchers.eq("user.email")))
                .thenReturn(java.util.Optional.of("john@example.com"));
        when(sqlEscapeUtil.formatValueForType(any(), org.mockito.ArgumentMatchers.eq("VARCHAR(255)")))
                .thenAnswer(invocation -> {
                    Object value = invocation.getArgument(0);
                    return "'" + value + "'";
                });
        
        String result = insertStatementGenerator.generateInsert(tableDefinition, jsonData);
        
        assertNotNull(result);
        assertTrue(result.contains("INSERT INTO"));
        assertTrue(result.contains("\"users\""));
        assertTrue(result.contains("\"username\""));
        assertTrue(result.contains("\"email\""));
        assertTrue(result.contains("'john_doe'"));
        assertTrue(result.contains("'john@example.com'"));
        assertFalse(result.contains("\"id\"")); // Auto-increment column should be excluded
    }
    
    @Test
    void testGenerateInsert_WithSchema() {
        tableDefinition.setSchema("public");
        String jsonData = "{\"user\":{\"name\":\"john_doe\"}}";
        
        when(jsonPathExtractor.extractValue(anyString(), org.mockito.ArgumentMatchers.eq("user.name")))
                .thenReturn(java.util.Optional.of("john_doe"));
        when(jsonPathExtractor.extractValue(anyString(), org.mockito.ArgumentMatchers.eq("user.email")))
                .thenReturn(java.util.Optional.empty());
        when(sqlEscapeUtil.formatValueForType(any(), anyString()))
                .thenAnswer(invocation -> {
                    Object value = invocation.getArgument(0);
                    if (value == null) return "NULL";
                    return "'" + value + "'";
                });
        
        String result = insertStatementGenerator.generateInsert(tableDefinition, jsonData);
        
        assertNotNull(result);
        assertTrue(result.contains("\"public\".\"users\""));
    }
    
    @Test
    void testGenerateInsert_WithDefaultValue() {
        ColumnDefinition columnWithDefault = ColumnDefinition.builder()
                .name("status")
                .type("VARCHAR(50)")
                .nullable(true)
                .defaultValue("active")
                .build();
        
        tableDefinition.getColumns().add(columnWithDefault);
        String jsonData = "{\"user\":{\"name\":\"john_doe\"}}";
        
        when(jsonPathExtractor.extractValue(anyString(), org.mockito.ArgumentMatchers.eq("user.name")))
                .thenReturn(java.util.Optional.of("john_doe"));
        when(jsonPathExtractor.extractValue(anyString(), org.mockito.ArgumentMatchers.eq("user.email")))
                .thenReturn(java.util.Optional.empty());
        when(jsonPathExtractor.extractValue(anyString(), org.mockito.ArgumentMatchers.eq("status")))
                .thenReturn(java.util.Optional.empty());
        when(sqlEscapeUtil.formatValueForType(any(), anyString()))
                .thenAnswer(invocation -> {
                    Object value = invocation.getArgument(0);
                    if (value == null) return "NULL";
                    return "'" + value + "'";
                });
        
        String result = insertStatementGenerator.generateInsert(tableDefinition, jsonData);
        assertNotNull(result);
    }
    
    @Test
    void testGenerateBatchInserts_SingleObject() {
        String jsonData = "{\"user\":{\"name\":\"john_doe\"}}";
        
        when(jsonPathExtractor.extractValue(anyString(), anyString()))
                .thenReturn(java.util.Optional.of("john_doe"));
        when(sqlEscapeUtil.formatValueForType(any(), anyString()))
                .thenReturn("'john_doe'");
        
        List<String> results = insertStatementGenerator.generateBatchInserts(tableDefinition, jsonData);
        
        assertEquals(1, results.size());
        assertTrue(results.get(0).contains("INSERT INTO"));
    }
    
    @Test
    void testGenerateBatchInserts_Array() {
        String jsonData = "[{\"user\":{\"name\":\"john_doe\"}},{\"user\":{\"name\":\"jane_doe\"}}]";
        
        when(jsonPathExtractor.extractValue(anyString(), anyString()))
                .thenReturn(java.util.Optional.of("test"));
        when(sqlEscapeUtil.formatValueForType(any(), anyString()))
                .thenReturn("'test'");
        
        List<String> results = insertStatementGenerator.generateBatchInserts(tableDefinition, jsonData);
        
        assertEquals(2, results.size());
        results.forEach(result -> assertTrue(result.contains("INSERT INTO")));
    }
    
}
