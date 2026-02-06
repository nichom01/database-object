package com.yourcompany.jsontosql.generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourcompany.jsontosql.model.ColumnDefinition;
import com.yourcompany.jsontosql.model.TableDefinition;
import com.yourcompany.jsontosql.util.JsonPathExtractor;
import com.yourcompany.jsontosql.util.SqlEscapeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InsertStatementGenerator {
    
    private final JsonPathExtractor jsonPathExtractor;
    private final SqlEscapeUtil sqlEscapeUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Generates an INSERT statement from JSON data and table definition
     */
    public String generateInsert(TableDefinition tableDefinition, String jsonData) {
        StringBuilder sql = new StringBuilder();
        
        String tableName = tableDefinition.getTableName();
        String schema = tableDefinition.getSchema();
        
        // Build table name with optional schema
        String fullTableName = schema != null && !schema.isEmpty() 
            ? sqlEscapeUtil.escapeIdentifier(schema) + "." + sqlEscapeUtil.escapeIdentifier(tableName)
            : sqlEscapeUtil.escapeIdentifier(tableName);
        
        sql.append("INSERT INTO ").append(fullTableName).append(" (");
        
        // Build column list (excluding auto-increment columns)
        List<ColumnDefinition> insertableColumns = new ArrayList<>();
        List<String> columnNames = new ArrayList<>();
        
        for (ColumnDefinition column : tableDefinition.getColumns()) {
            if (!column.getAutoIncrement()) {
                insertableColumns.add(column);
                columnNames.add(sqlEscapeUtil.escapeIdentifier(column.getName()));
            }
        }
        
        sql.append(String.join(", ", columnNames));
        sql.append(") VALUES (");
        
        // Build values list
        List<String> values = new ArrayList<>();
        for (ColumnDefinition column : insertableColumns) {
            Object value = extractColumnValue(column, jsonData);
            String formattedValue = sqlEscapeUtil.formatValueForType(value, column.getType());
            values.add(formattedValue);
        }
        
        sql.append(String.join(", ", values));
        sql.append(");");
        
        return sql.toString();
    }
    
    /**
     * Generates batch INSERT statements from an array of JSON objects
     */
    public List<String> generateBatchInserts(TableDefinition tableDefinition, String jsonData) {
        List<String> statements = new ArrayList<>();
        
        try {
            JsonNode rootNode = objectMapper.readTree(jsonData);
            
            if (rootNode.isArray()) {
                for (JsonNode jsonNode : rootNode) {
                    String singleJson = objectMapper.writeValueAsString(jsonNode);
                    statements.add(generateInsert(tableDefinition, singleJson));
                }
            } else {
                // Single object
                statements.add(generateInsert(tableDefinition, jsonData));
            }
        } catch (Exception e) {
            log.error("Error generating batch inserts: {}", e.getMessage());
            throw new RuntimeException("Failed to generate batch inserts", e);
        }
        
        return statements;
    }
    
    /**
     * Extracts a value for a column from JSON data
     */
    private Object extractColumnValue(ColumnDefinition column, String jsonData) {
        // If jsonPath is specified, use it
        if (column.getJsonPath() != null && !column.getJsonPath().trim().isEmpty()) {
            return jsonPathExtractor.extractValue(jsonData, column.getJsonPath())
                    .orElse(column.getDefaultValue());
        }
        
        // Otherwise, try to extract by column name directly
        String columnName = column.getName();
        Object value = jsonPathExtractor.extractValue(jsonData, columnName)
                .orElse(null);
        
        // If still not found and default value is set, use it
        if (value == null && column.getDefaultValue() != null) {
            return column.getDefaultValue();
        }
        
        return value;
    }
}
