package com.yourcompany.jsontosql.generator;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SqlScriptGeneratorTest {
    
    @Mock
    private InsertStatementGenerator insertStatementGenerator;
    
    @Mock
    private DdlGenerator ddlGenerator;
    
    @InjectMocks
    private SqlScriptGenerator sqlScriptGenerator;
    
    private TableDefinition tableDefinition;
    private SqlGenerationRequest request;
    
    @BeforeEach
    void setUp() {
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
        
        request = SqlGenerationRequest.builder()
                .tableName("users")
                .jsonData("{\"user\":{\"name\":\"john_doe\"}}")
                .includeDdl(false)
                .batchMode(false)
                .build();
    }
    
    @Test
    void testGenerateScript_WithoutDdl() {
        when(insertStatementGenerator.generateInsert(any(TableDefinition.class), anyString()))
                .thenReturn("INSERT INTO \"users\" (\"username\") VALUES ('john_doe');");
        
        SqlGenerationResponse response = sqlScriptGenerator.generateScript(request, tableDefinition);
        
        assertNotNull(response);
        assertEquals("users", response.getTableName());
        assertEquals(1, response.getStatementCount());
        assertTrue(response.getSqlScript().contains("INSERT INTO"));
        assertFalse(response.getSqlScript().contains("CREATE TABLE"));
    }
    
    @Test
    void testGenerateScript_WithDdl() {
        request.setIncludeDdl(true);
        
        when(ddlGenerator.generateCreateTable(any(TableDefinition.class)))
                .thenReturn("CREATE TABLE \"users\" (\"username\" VARCHAR(255) NOT NULL);");
        when(insertStatementGenerator.generateInsert(any(TableDefinition.class), anyString()))
                .thenReturn("INSERT INTO \"users\" (\"username\") VALUES ('john_doe');");
        
        SqlGenerationResponse response = sqlScriptGenerator.generateScript(request, tableDefinition);
        
        assertNotNull(response);
        assertEquals(2, response.getStatementCount());
        assertTrue(response.getSqlScript().contains("CREATE TABLE"));
        assertTrue(response.getSqlScript().contains("INSERT INTO"));
    }
    
    @Test
    void testGenerateScript_BatchMode() {
        request.setBatchMode(true);
        
        when(insertStatementGenerator.generateBatchInserts(any(TableDefinition.class), anyString()))
                .thenReturn(Arrays.asList(
                        "INSERT INTO \"users\" (\"username\") VALUES ('john_doe');",
                        "INSERT INTO \"users\" (\"username\") VALUES ('jane_doe');"
                ));
        
        SqlGenerationResponse response = sqlScriptGenerator.generateScript(request, tableDefinition);
        
        assertNotNull(response);
        assertEquals(2, response.getStatementCount());
        assertEquals(2, response.getStatements().size());
    }
    
    @Test
    void testGenerateScript_WithDdlAndBatch() {
        request.setIncludeDdl(true);
        request.setBatchMode(true);
        
        when(ddlGenerator.generateCreateTable(any(TableDefinition.class)))
                .thenReturn("CREATE TABLE \"users\" (\"username\" VARCHAR(255) NOT NULL);");
        when(insertStatementGenerator.generateBatchInserts(any(TableDefinition.class), anyString()))
                .thenReturn(Arrays.asList(
                        "INSERT INTO \"users\" (\"username\") VALUES ('john_doe');",
                        "INSERT INTO \"users\" (\"username\") VALUES ('jane_doe');"
                ));
        
        SqlGenerationResponse response = sqlScriptGenerator.generateScript(request, tableDefinition);
        
        assertNotNull(response);
        assertEquals(3, response.getStatementCount()); // 1 DDL + 2 INSERTs
        assertTrue(response.getSqlScript().contains("CREATE TABLE"));
        assertTrue(response.getSqlScript().contains("INSERT INTO"));
    }
}
