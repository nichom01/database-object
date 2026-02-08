package com.yourcompany.jsontosql.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class TableDefinition {
    
    @NotBlank(message = "Table name is required")
    @JsonProperty("tableName")
    private String tableName;
    
    @NotEmpty(message = "At least one column definition is required")
    @Valid
    private List<ColumnDefinition> columns;
    
    private String schema; // Optional schema name (e.g., "public" for PostgreSQL)
    
    private String description; // Optional description of the table
    
    // Constructors
    public TableDefinition() {
    }
    
    public TableDefinition(String tableName, List<ColumnDefinition> columns, String schema, String description) {
        this.tableName = tableName;
        this.columns = columns;
        this.schema = schema;
        this.description = description;
    }
    
    // Getters and Setters
    public String getTableName() {
        return tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public List<ColumnDefinition> getColumns() {
        return columns;
    }
    
    public void setColumns(List<ColumnDefinition> columns) {
        this.columns = columns;
    }
    
    public String getSchema() {
        return schema;
    }
    
    public void setSchema(String schema) {
        this.schema = schema;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String tableName;
        private List<ColumnDefinition> columns;
        private String schema;
        private String description;
        
        public Builder tableName(String tableName) {
            this.tableName = tableName;
            return this;
        }
        
        public Builder columns(List<ColumnDefinition> columns) {
            this.columns = columns;
            return this;
        }
        
        public Builder schema(String schema) {
            this.schema = schema;
            return this;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public TableDefinition build() {
            return new TableDefinition(tableName, columns, schema, description);
        }
    }
}
