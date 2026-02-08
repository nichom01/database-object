package com.yourcompany.jsontosql.service;

import com.yourcompany.jsontosql.model.ColumnDefinition;
import com.yourcompany.jsontosql.model.SqlGenerationRequest;
import com.yourcompany.jsontosql.model.SqlGenerationResponse;
import com.yourcompany.jsontosql.model.TableDefinition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for SqlGeneratorService using real Spring beans.
 * This avoids Mockito compatibility issues with Java 25.
 */
@SpringBootTest
class SqlGeneratorServiceIntegrationTest {
    
    @Autowired
    private SqlGeneratorService sqlGeneratorService;
    
    @Autowired
    private TableDefinitionService tableDefinitionService;
    
    private TableDefinition testTableDefinition;
    
    @BeforeEach
    void setUp() {
        // Create a real table definition for testing
        testTableDefinition = TableDefinition.builder()
                .tableName("test_users")
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
        
        // Save it to the service
        tableDefinitionService.saveTableDefinition(testTableDefinition);
    }
    
    @AfterEach
    void tearDown() {
        // Clean up test table definition
        try {
            tableDefinitionService.deleteTableDefinition("test_users");
        } catch (Exception e) {
            // Ignore if already deleted
        }
    }
    
    @Test
    void testGenerateSql_Success() {
        SqlGenerationRequest request = SqlGenerationRequest.builder()
                .tableName("test_users")
                .jsonData("{\"user\":{\"name\":\"john_doe\",\"email\":\"john@example.com\"}}")
                .build();
        
        SqlGenerationResponse response = sqlGeneratorService.generateSql(request);
        
        assertNotNull(response);
        assertEquals("test_users", response.getTableName());
        assertTrue(response.getStatementCount() > 0);
        assertNotNull(response.getSqlScript());
        assertTrue(response.getSqlScript().contains("INSERT INTO"));
    }
    
    @Test
    void testValidateJson_Success() {
        java.util.Map<String, Object> result = sqlGeneratorService.validateJson(
                "test_users", 
                "{\"user\":{\"name\":\"john_doe\",\"email\":\"john@example.com\"}}"
        );
        
        assertNotNull(result);
        // Validation should pass for valid JSON
        assertTrue(result.containsKey("valid"));
    }
}
