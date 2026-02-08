package com.yourcompany.jsontosql.model;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;

public class SqlGenerationRequest {
    
    @NotBlank(message = "Table name is required")
    private String tableName;
    
    @NotNull(message = "JSON data is required")
    @JsonRawValue
    @JsonSerialize(using = RawJsonSerializer.class)
    @JsonDeserialize(using = RawJsonDeserializer.class)
    private String jsonData; // Raw JSON string
    
    // Custom serializer/deserializer for @JsonRawValue
    public static class RawJsonSerializer extends JsonSerializer<String> {
        @Override
        public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null) {
                gen.writeNull();
            } else {
                gen.writeRawValue(value);
            }
        }
    }
    
    public static class RawJsonDeserializer extends JsonDeserializer<String> {
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            // Read the JSON as a tree and convert to string
            com.fasterxml.jackson.databind.JsonNode node = p.getCodec().readTree(p);
            return node.toString();
        }
    }
    
    private Boolean includeDdl = false; // Whether to include CREATE TABLE statement
    
    private Boolean batchMode = false; // If true, expects array of JSON objects
    
    private String dialect = "STANDARD"; // SQL dialect: STANDARD, MYSQL, POSTGRESQL, ORACLE
    
    // Constructors
    public SqlGenerationRequest() {
    }
    
    public SqlGenerationRequest(String tableName, String jsonData, Boolean includeDdl, 
                                Boolean batchMode, String dialect) {
        this.tableName = tableName;
        this.jsonData = jsonData;
        this.includeDdl = includeDdl != null ? includeDdl : false;
        this.batchMode = batchMode != null ? batchMode : false;
        this.dialect = dialect != null ? dialect : "STANDARD";
    }
    
    // Getters and Setters
    public String getTableName() {
        return tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public String getJsonData() {
        return jsonData;
    }
    
    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }
    
    public Boolean getIncludeDdl() {
        return includeDdl;
    }
    
    public void setIncludeDdl(Boolean includeDdl) {
        this.includeDdl = includeDdl;
    }
    
    public Boolean getBatchMode() {
        return batchMode;
    }
    
    public void setBatchMode(Boolean batchMode) {
        this.batchMode = batchMode;
    }
    
    public String getDialect() {
        return dialect;
    }
    
    public void setDialect(String dialect) {
        this.dialect = dialect;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String tableName;
        private String jsonData;
        private Boolean includeDdl = false;
        private Boolean batchMode = false;
        private String dialect = "STANDARD";
        
        public Builder tableName(String tableName) {
            this.tableName = tableName;
            return this;
        }
        
        public Builder jsonData(String jsonData) {
            this.jsonData = jsonData;
            return this;
        }
        
        public Builder includeDdl(Boolean includeDdl) {
            this.includeDdl = includeDdl;
            return this;
        }
        
        public Builder batchMode(Boolean batchMode) {
            this.batchMode = batchMode;
            return this;
        }
        
        public Builder dialect(String dialect) {
            this.dialect = dialect;
            return this;
        }
        
        public SqlGenerationRequest build() {
            return new SqlGenerationRequest(tableName, jsonData, includeDdl, batchMode, dialect);
        }
    }
}
