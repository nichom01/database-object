package com.yourcompany.jsontosql.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourcompany.jsontosql.model.SqlGenerationRequest;
import com.yourcompany.jsontosql.model.TableDefinition;
import com.yourcompany.jsontosql.service.SqlGeneratorService;
import com.yourcompany.jsontosql.service.TableDefinitionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SqlGeneratorController.class)
class SqlGeneratorControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private SqlGeneratorService sqlGeneratorService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        // Setup test table definition
        TableDefinition tableDefinition = TableDefinition.builder()
                .tableName("users")
                .columns(Arrays.asList(
                        com.yourcompany.jsontosql.model.ColumnDefinition.builder()
                                .name("id")
                                .type("BIGINT")
                                .nullable(false)
                                .primaryKey(true)
                                .autoIncrement(true)
                                .build(),
                        com.yourcompany.jsontosql.model.ColumnDefinition.builder()
                                .name("username")
                                .type("VARCHAR(255)")
                                .nullable(false)
                                .jsonPath("user.name")
                                .build()
                ))
                .build();
    }
    
    @Test
    void testGenerateSql() throws Exception {
        SqlGenerationRequest request = SqlGenerationRequest.builder()
                .tableName("users")
                .jsonData("{\"user\":{\"name\":\"john_doe\",\"email\":\"john@example.com\"}}")
                .build();
        
        com.yourcompany.jsontosql.model.SqlGenerationResponse response = 
                com.yourcompany.jsontosql.model.SqlGenerationResponse.builder()
                        .sqlScript("INSERT INTO \"users\" (\"username\") VALUES ('john_doe');")
                        .tableName("users")
                        .statementCount(1)
                        .build();
        
        when(sqlGeneratorService.generateSql(any(SqlGenerationRequest.class))).thenReturn(response);
        
        mockMvc.perform(post("/api/v1/sql/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tableName").value("users"))
                .andExpect(jsonPath("$.statementCount").value(1));
        
        verify(sqlGeneratorService).generateSql(any(SqlGenerationRequest.class));
    }
    
    @Test
    void testGenerateSql_InvalidRequest() throws Exception {
        SqlGenerationRequest request = SqlGenerationRequest.builder()
                .tableName("") // Invalid: empty table name
                .jsonData("{\"user\":{\"name\":\"john_doe\"}}")
                .build();
        
        mockMvc.perform(post("/api/v1/sql/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testValidateJson() throws Exception {
        java.util.Map<String, Object> validationResult = java.util.Map.of(
                "valid", true,
                "errors", java.util.List.of()
        );
        
        when(sqlGeneratorService.validateJson(anyString(), anyString()))
                .thenReturn(validationResult);
        
        mockMvc.perform(post("/api/v1/sql/validate")
                        .param("tableName", "users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user\":{\"name\":\"john_doe\"}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
        
        verify(sqlGeneratorService).validateJson(eq("users"), anyString());
    }
}
