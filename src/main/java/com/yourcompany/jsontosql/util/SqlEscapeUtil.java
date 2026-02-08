package com.yourcompany.jsontosql.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class SqlEscapeUtil {
    
    private static final Logger log = LoggerFactory.getLogger(SqlEscapeUtil.class);
    
    /**
     * Escapes a string value for SQL insertion
     */
    public String escapeString(Object value) {
        if (value == null) {
            return "NULL";
        }
        
        // Handle different types
        if (value instanceof String) {
            return escapeStringValue((String) value);
        } else if (value instanceof Number) {
            return value.toString();
        } else if (value instanceof Boolean) {
            return ((Boolean) value) ? "1" : "0";
        } else if (value instanceof LocalDate) {
            return "'" + ((LocalDate) value).format(DateTimeFormatter.ISO_LOCAL_DATE) + "'";
        } else if (value instanceof LocalTime) {
            return "'" + ((LocalTime) value).format(DateTimeFormatter.ISO_LOCAL_TIME) + "'";
        } else if (value instanceof LocalDateTime) {
            return "'" + ((LocalDateTime) value).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "'";
        } else if (value instanceof Date) {
            return "'" + ((Date) value).toString() + "'";
        } else if (value instanceof Timestamp) {
            return "'" + ((Timestamp) value).toString() + "'";
        } else {
            // Default: convert to string and escape
            return escapeStringValue(value.toString());
        }
    }
    
    /**
     * Escapes a string value, handling SQL injection prevention
     */
    private String escapeStringValue(String value) {
        if (value == null) {
            return "NULL";
        }
        
        // Replace single quotes with two single quotes (SQL standard)
        String escaped = value.replace("'", "''");
        return "'" + escaped + "'";
    }
    
    /**
     * Escapes an identifier (table name, column name) with quotes if needed
     */
    public String escapeIdentifier(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new IllegalArgumentException("Identifier cannot be null or empty");
        }
        
        // Remove any existing quotes and add new ones
        String cleaned = identifier.trim().replace("\"", "").replace("`", "").replace("[", "").replace("]", "");
        return "\"" + cleaned + "\"";
    }
    
    /**
     * Formats a value according to SQL type
     */
    public String formatValueForType(Object value, String sqlType) {
        if (value == null) {
            return "NULL";
        }
        
        String upperType = sqlType.toUpperCase();
        
        // Handle numeric types
        if (upperType.contains("INT") || upperType.contains("DECIMAL") || 
            upperType.contains("NUMERIC") || upperType.contains("FLOAT") || 
            upperType.contains("DOUBLE") || upperType.contains("REAL")) {
            if (value instanceof Number) {
                return value.toString();
            }
            // Try to parse as number
            try {
                Double.parseDouble(value.toString());
                return value.toString();
            } catch (NumberFormatException e) {
                log.warn("Value '{}' cannot be converted to numeric type {}", value, sqlType);
                return escapeString(value);
            }
        }
        
        // Handle boolean types
        if (upperType.contains("BOOLEAN") || upperType.contains("BIT")) {
            if (value instanceof Boolean) {
                return ((Boolean) value) ? "1" : "0";
            }
            String strValue = value.toString().toLowerCase();
            if ("true".equals(strValue) || "1".equals(strValue) || "yes".equals(strValue)) {
                return "1";
            }
            return "0";
        }
        
        // Default: escape as string
        return escapeString(value);
    }
}
