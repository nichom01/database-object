package com.yourcompany.jsontosql.exception;

public class TableDefinitionNotFoundException extends RuntimeException {
    
    public TableDefinitionNotFoundException(String tableName) {
        super("Table definition not found: " + tableName);
    }
    
    public TableDefinitionNotFoundException(String tableName, Throwable cause) {
        super("Table definition not found: " + tableName, cause);
    }
}
