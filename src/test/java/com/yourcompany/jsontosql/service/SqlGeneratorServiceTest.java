package com.yourcompany.jsontosql.service;

import com.yourcompany.jsontosql.generator.SqlScriptGenerator;
import com.yourcompany.jsontosql.model.ColumnDefinition;
import com.yourcompany.jsontosql.model.SqlGenerationRequest;
import com.yourcompany.jsontosql.model.SqlGenerationResponse;
import com.yourcompany.jsontosql.model.TableDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SqlGeneratorServiceTest {
    
    @Mock
    private TableDefinitionService tableDefinitionService;
    
    @Mock
    private JsonMappingService jsonMappingService;
    
    @Mock
    private SqlScriptGenerator sqlScriptGenerator;
    
    @InjectMocks
    private SqlGeneratorService sqlGeneratorService;
    
    private SqlGenerationRequest request;
    private TableDefinition tableDefinition;
    
    @BeforeEach
    void setUp() {
        request = SqlGenerationRequest.builder()
                .tableName("users")
                .jsonData("{\"user\":{\"name\":\"john_doe\"}}")
                .build();
        
        tableDefinition = TableDefinition.builder()
                .tableName("users")
                .columns(Arrays.asList(
                        ColumnDefinition.builder()
                                .name("username")
                                .type("VARCHAR(255)")
                                .nullable(false)
                                .build()
                ))
                .build();
    }
    
    @Test
    void testGenerateSql_Success() {
        SqlGenerationResponse expectedResponse = SqlGenerationResponse.builder()
                .sqlScript("INSERT INTO \"users\" (\"username\") VALUES ('john_doe');")
                .tableName("users")
                .statementCount(1)
                .build();
        
        when(tableDefinitionService.getTableDefinition("users")).thenReturn(tableDefinition);
        when(jsonMappingService.validateJsonAgainstSchema(any(TableDefinition.class), anyString()))
                .thenReturn(java.util.Map.of("valid", true));
        when(sqlScriptGenerator.generateScript(any(SqlGenerationRequest.class), any(TableDefinition.class)))
                .thenReturn(expectedResponse);
        
        SqlGenerationResponse response = sqlGeneratorService.generateSql(request);
        
        assertNotNull(response);
        assertEquals("users", response.getTableName());
        assertEquals(1, response.getStatementCount());
        
        verify(tableDefinitionService).getTableDefinition("users");
        verify(jsonMappingService).validateJsonAgainstSchema(any(TableDefinition.class), anyString());
        verify(sqlScriptGenerator).generateScript(any(SqlGenerationRequest.class), any(TableDefinition.class));
    }
    
    @Test
    void testValidateJson_Success() {
        Map<String, Object> validationResult = java.util.Map.of(
                "valid", true,
                "errors", java.util.List.of()
        );
        
        when(tableDefinitionService.getTableDefinition("users")).thenReturn(tableDefinition);
        when(jsonMappingService.validateJsonAgainstSchema(any(TableDefinition.class), anyString()))
                .thenReturn(validationResult);
        
        Map<String, Object> result = sqlGeneratorService.validateJson("users", "{\"user\":{\"name\":\"john_doe\"}}");
        
        assertNotNull(result);
        assertTrue((Boolean) result.get("valid"));
        
        verify(tableDefinitionService).getTableDefinition("users");
        verify(jsonMappingService).validateJsonAgainstSchema(any(TableDefinition.class), anyString());
    }
}
