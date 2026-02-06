package com.yourcompany.jsontosql.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourcompany.jsontosql.exception.JsonMappingException;
import com.yourcompany.jsontosql.model.ColumnDefinition;
import com.yourcompany.jsontosql.model.TableDefinition;
import com.yourcompany.jsontosql.util.JsonPathExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsonMappingService {
    
    private final JsonPathExtractor jsonPathExtractor;
    private final ObjectMapper objectMapper;
    
    /**
     * Validates JSON data against table definition
     */
    public Map<String, Object> validateJsonAgainstSchema(TableDefinition tableDefinition, String jsonData) {
        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        Map<String, Object> extractedValues = new HashMap<>();
        
        try {
            // Validate JSON is valid
            JsonNode jsonNode = objectMapper.readTree(jsonData);
            
            // Check each column
            for (ColumnDefinition column : tableDefinition.getColumns()) {
                String columnName = column.getName();
                Object value = null;
                
                // Try to extract value using jsonPath if specified
                if (column.getJsonPath() != null && !column.getJsonPath().trim().isEmpty()) {
                    value = jsonPathExtractor.extractValue(jsonData, column.getJsonPath()).orElse(null);
                } else {
                    // Try direct column name
                    value = jsonPathExtractor.extractValue(jsonData, columnName).orElse(null);
                }
                
                extractedValues.put(columnName, value);
                
                // Validate required fields
                if (!column.getNullable() && value == null && column.getDefaultValue() == null) {
                    errors.add("Column '" + columnName + "' is required but value is missing");
                }
                
                // Type validation could be added here
            }
            
            result.put("valid", errors.isEmpty());
            result.put("errors", errors);
            result.put("warnings", warnings);
            result.put("extractedValues", extractedValues);
            
        } catch (Exception e) {
            throw new JsonMappingException("Failed to validate JSON: " + e.getMessage(), e);
        }
        
        return result;
    }
    
    /**
     * Maps JSON data to column values
     */
    public Map<String, Object> mapJsonToColumns(TableDefinition tableDefinition, String jsonData) {
        Map<String, Object> columnValues = new HashMap<>();
        
        for (ColumnDefinition column : tableDefinition.getColumns()) {
            Object value = null;
            
            // Try jsonPath first
            if (column.getJsonPath() != null && !column.getJsonPath().trim().isEmpty()) {
                value = jsonPathExtractor.extractValue(jsonData, column.getJsonPath())
                        .orElse(column.getDefaultValue());
            } else {
                // Try direct column name
                value = jsonPathExtractor.extractValue(jsonData, column.getName())
                        .orElse(column.getDefaultValue());
            }
            
            columnValues.put(column.getName(), value);
        }
        
        return columnValues;
    }
}
