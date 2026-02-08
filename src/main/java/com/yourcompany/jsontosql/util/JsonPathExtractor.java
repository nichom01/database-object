package com.yourcompany.jsontosql.util;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JsonPathExtractor {
    
    private static final Logger log = LoggerFactory.getLogger(JsonPathExtractor.class);
    
    /**
     * Extracts a value from JSON using a JSONPath expression
     * 
     * @param jsonData The JSON string to extract from
     * @param jsonPath The JSONPath expression (e.g., "$.user.name", "user.email")
     * @return Optional containing the extracted value, or empty if not found
     */
    public Optional<Object> extractValue(String jsonData, String jsonPath) {
        if (jsonData == null || jsonPath == null || jsonPath.trim().isEmpty()) {
            return Optional.empty();
        }
        
        try {
            // Normalize JSONPath - ensure it starts with $ if it doesn't
            String normalizedPath = jsonPath.trim();
            if (!normalizedPath.startsWith("$")) {
                normalizedPath = "$." + normalizedPath;
            }
            
            Object value = JsonPath.read(jsonData, normalizedPath);
            return Optional.ofNullable(value);
        } catch (PathNotFoundException e) {
            log.debug("JSONPath '{}' not found in JSON data", jsonPath);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error extracting value using JSONPath '{}': {}", jsonPath, e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Extracts a value and converts it to String
     */
    public Optional<String> extractStringValue(String jsonData, String jsonPath) {
        return extractValue(jsonData, jsonPath)
                .map(value -> value == null ? null : value.toString());
    }
    
    /**
     * Checks if a JSONPath exists in the JSON data
     */
    public boolean pathExists(String jsonData, String jsonPath) {
        return extractValue(jsonData, jsonPath).isPresent();
    }
}
