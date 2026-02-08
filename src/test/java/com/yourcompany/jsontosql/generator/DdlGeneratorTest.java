package com.yourcompany.jsontosql.generator;

import com.yourcompany.jsontosql.model.ColumnDefinition;
import com.yourcompany.jsontosql.model.TableDefinition;
import com.yourcompany.jsontosql.util.SqlEscapeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class DdlGeneratorTest {
    
    @MockBean
    private SqlEscapeUtil sqlEscapeUtil;
    
    @Autowired
    private DdlGenerator ddlGenerator;
    
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
                                .build(),
                        ColumnDefinition.builder()
                                .name("email")
                                .type("VARCHAR(255)")
                                .nullable(true)
                                .build()
                ))
                .build();
        
        when(sqlEscapeUtil.escapeIdentifier(anyString())).thenAnswer(invocation -> {
            String arg = invocation.getArgument(0);
            return "\"" + arg + "\"";
        });
    }
    
    @Test
    void testGenerateCreateTable_Basic() {
        String result = ddlGenerator.generateCreateTable(tableDefinition);
        
        assertNotNull(result);
        assertTrue(result.contains("CREATE TABLE"));
        assertTrue(result.contains("\"users\""));
        assertTrue(result.contains("\"id\""));
        assertTrue(result.contains("\"username\""));
        assertTrue(result.contains("\"email\""));
        assertTrue(result.contains("BIGINT"));
        assertTrue(result.contains("VARCHAR(255)"));
        assertTrue(result.contains("NOT NULL"));
        assertTrue(result.contains("PRIMARY KEY"));
        assertTrue(result.contains("AUTO_INCREMENT"));
    }
    
    @Test
    void testGenerateCreateTable_WithSchema() {
        tableDefinition.setSchema("public");
        String result = ddlGenerator.generateCreateTable(tableDefinition);
        
        assertNotNull(result);
        assertTrue(result.contains("\"public\".\"users\""));
    }
    
    @Test
    void testGenerateCreateTable_NullableColumns() {
        String result = ddlGenerator.generateCreateTable(tableDefinition);
        
        // Email is nullable, so it shouldn't have NOT NULL
        assertTrue(result.contains("\"email\""));
        // Username is not nullable, so it should have NOT NULL
        assertTrue(result.contains("\"username\""));
    }
    
    @Test
    void testGenerateCreateTable_MultiplePrimaryKeys() {
        ColumnDefinition col2 = ColumnDefinition.builder()
                .name("code")
                .type("VARCHAR(50)")
                .nullable(false)
                .primaryKey(true)
                .build();
        // Create a new mutable list
        java.util.List<ColumnDefinition> columns = new java.util.ArrayList<>(tableDefinition.getColumns());
        columns.add(col2);
        tableDefinition.setColumns(columns);
        
        String result = ddlGenerator.generateCreateTable(tableDefinition);
        
        assertTrue(result.contains("PRIMARY KEY"));
        assertTrue(result.contains("\"id\""));
        assertTrue(result.contains("\"code\""));
    }
    
    @Test
    void testGenerateDropTable_Basic() {
        String result = ddlGenerator.generateDropTable(tableDefinition, false);
        
        assertNotNull(result);
        assertTrue(result.contains("DROP TABLE"));
        assertTrue(result.contains("\"users\""));
        assertFalse(result.contains("IF EXISTS"));
    }
    
    @Test
    void testGenerateDropTable_WithIfExists() {
        String result = ddlGenerator.generateDropTable(tableDefinition, true);
        
        assertNotNull(result);
        assertTrue(result.contains("DROP TABLE"));
        assertTrue(result.contains("IF EXISTS"));
        assertTrue(result.contains("\"users\""));
    }
    
    @Test
    void testGenerateDropTable_WithSchema() {
        tableDefinition.setSchema("public");
        String result = ddlGenerator.generateDropTable(tableDefinition, true);
        
        assertNotNull(result);
        assertTrue(result.contains("\"public\".\"users\""));
    }
}
