package com.yourcompany.jsontosql.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ColumnDefinition {
    
    @NotBlank(message = "Column name is required")
    private String name;
    
    @NotBlank(message = "Column type is required")
    private String type;
    
    @NotNull(message = "Nullable flag is required")
    private Boolean nullable = true;
    
    private Boolean primaryKey = false;
    
    private Boolean autoIncrement = false;
    
    @JsonProperty("jsonPath")
    private String jsonPath; // JSONPath expression to extract value from input JSON
    
    private String defaultValue; // Default value if jsonPath doesn't resolve
    
    private Integer maxLength; // For VARCHAR types
    
    private Integer precision; // For DECIMAL types
    
    private Integer scale; // For DECIMAL types
    
    // Constructors
    public ColumnDefinition() {
    }
    
    public ColumnDefinition(String name, String type, Boolean nullable, Boolean primaryKey, 
                           Boolean autoIncrement, String jsonPath, String defaultValue, 
                           Integer maxLength, Integer precision, Integer scale) {
        this.name = name;
        this.type = type;
        this.nullable = nullable != null ? nullable : true;
        this.primaryKey = primaryKey != null ? primaryKey : false;
        this.autoIncrement = autoIncrement != null ? autoIncrement : false;
        this.jsonPath = jsonPath;
        this.defaultValue = defaultValue;
        this.maxLength = maxLength;
        this.precision = precision;
        this.scale = scale;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Boolean getNullable() {
        return nullable;
    }
    
    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }
    
    public Boolean getPrimaryKey() {
        return primaryKey;
    }
    
    public void setPrimaryKey(Boolean primaryKey) {
        this.primaryKey = primaryKey;
    }
    
    public Boolean getAutoIncrement() {
        return autoIncrement;
    }
    
    public void setAutoIncrement(Boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }
    
    public String getJsonPath() {
        return jsonPath;
    }
    
    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public Integer getMaxLength() {
        return maxLength;
    }
    
    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }
    
    public Integer getPrecision() {
        return precision;
    }
    
    public void setPrecision(Integer precision) {
        this.precision = precision;
    }
    
    public Integer getScale() {
        return scale;
    }
    
    public void setScale(Integer scale) {
        this.scale = scale;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String name;
        private String type;
        private Boolean nullable = true;
        private Boolean primaryKey = false;
        private Boolean autoIncrement = false;
        private String jsonPath;
        private String defaultValue;
        private Integer maxLength;
        private Integer precision;
        private Integer scale;
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder type(String type) {
            this.type = type;
            return this;
        }
        
        public Builder nullable(Boolean nullable) {
            this.nullable = nullable;
            return this;
        }
        
        public Builder primaryKey(Boolean primaryKey) {
            this.primaryKey = primaryKey;
            return this;
        }
        
        public Builder autoIncrement(Boolean autoIncrement) {
            this.autoIncrement = autoIncrement;
            return this;
        }
        
        public Builder jsonPath(String jsonPath) {
            this.jsonPath = jsonPath;
            return this;
        }
        
        public Builder defaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }
        
        public Builder maxLength(Integer maxLength) {
            this.maxLength = maxLength;
            return this;
        }
        
        public Builder precision(Integer precision) {
            this.precision = precision;
            return this;
        }
        
        public Builder scale(Integer scale) {
            this.scale = scale;
            return this;
        }
        
        public ColumnDefinition build() {
            return new ColumnDefinition(name, type, nullable, primaryKey, autoIncrement, 
                                      jsonPath, defaultValue, maxLength, precision, scale);
        }
    }
}
